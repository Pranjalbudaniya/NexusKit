package com.nexuskit.app.feature.tools.screen_light

import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.graphics.Color
import kotlin.math.sin

enum class LightMode { SCREEN, FLASHLIGHT, STROBE, SOS, MORSE }

data class ScreenColorPreset(val name: String, val color: Color)

data class MorseSymbol(
    val char: Char,
    val morse: String
)

object ScreenLightEngine {

    val presets = listOf(
        ScreenColorPreset("Pure White", Color(0xFFFFFFFF)),
        ScreenColorPreset("Warm Amber", Color(0xFFFFB300)),
        ScreenColorPreset("Emergency Red", Color(0xFFD32F2F)),
        ScreenColorPreset("Soft Blue", Color(0xFF2196F3)),
        ScreenColorPreset("Neon Green", Color(0xFF4CAF50)),
        ScreenColorPreset("Night Purple", Color(0xFF9C27B0))
    )

    private val morseMap = mapOf(
        'A' to ".-",    'B' to "-...",  'C' to "-.-.",  'D' to "-..",
        'E' to ".",     'F' to "..-.",  'G' to "--.",   'H' to "....",
        'I' to "..",    'J' to ".---",  'K' to "-.-",   'L' to ".-..",
        'M' to "--",    'N' to "-.",    'O' to "---",   'P' to ".--.",
        'Q' to "--.-",  'R' to ".-.",   'S' to "...",   'T' to "-",
        'U' to "..-",   'V' to "...-",  'W' to ".--",   'X' to "-..-",
        'Y' to "-.--",  'Z' to "--..",
        '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--",
        '4' to "....-", '5' to ".....", '6' to "-....", '7' to "--...",
        '8' to "---..", '9' to "----.",
        '.' to ".-.-.-", ',' to "--..--", '?' to "..--..", '\'' to ".----.",
        '!' to "-.-.--", '/' to "-..-.", '(' to "-.--.", ')' to "-.--.-",
        '&' to ".-...", ':' to "---...", ';' to "-.-.-.", '=' to "-...-",
        '+' to ".-.-.", '-' to "-....-", '_' to "..--.-", '"' to ".-..-.",
        '$' to "...-..-", '@' to ".--.-.", ' ' to "/"
    )

    fun charToMorse(char: Char): String {
        return morseMap[char.uppercaseChar()] ?: ""
    }

    fun textToMorse(text: String): String {
        return text.uppercase().map { char ->
            if (char == ' ') "/" else (morseMap[char] ?: "")
        }.filter { it.isNotEmpty() }.joinToString(" ")
    }

    fun textToMorseSymbols(text: String): List<MorseSymbol> {
        return text.map { char ->
            val upper = char.uppercaseChar()
            val morse = if (upper == ' ') "/" else (morseMap[upper] ?: "?")
            MorseSymbol(char = char, morse = morse)
        }
    }

    /** Converts Words Per Minute (WPM) to base dot unit duration in ms (PARIS standard: 1200 / WPM) */
    fun wpmToDotDurationMs(wpm: Int): Long {
        return (1200L / wpm.coerceIn(5, 40)).coerceAtLeast(30L)
    }

    fun toggleCameraFlashlight(context: Context, enable: Boolean): Boolean {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return false
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return false
            cameraManager.setTorchMode(cameraId, enable)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun vibrateDevice(context: Context, durationMs: Long) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs.coerceAtLeast(10L), VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs.coerceAtLeast(10L))
            }
        } catch (_: Exception) {}
    }
}

/**
 * Lightweight, low-latency audio sine-wave tone synthesizer for Morse code.
 * Synthesizes pure 800Hz sound in-memory with zero disk reads.
 */
class MorseAudioPlayer {
    private var audioTrack: AudioTrack? = null
    private val sampleRate = 44100
    private val frequency = 800.0 // 800 Hz pitch

    fun playTone(durationMs: Long) {
        try {
            val numSamples = ((sampleRate * durationMs) / 1000).toInt().coerceAtLeast(100)
            val buffer = ShortArray(numSamples)
            val twoPiF = 2.0 * Math.PI * frequency

            for (i in 0 until numSamples) {
                // Apply slight envelope (5ms attack/decay) to prevent clicking sounds
                val attackSamples = (sampleRate * 0.005).toInt()
                val envelope = when {
                    i < attackSamples -> i.toDouble() / attackSamples
                    i > numSamples - attackSamples -> (numSamples - i).toDouble() / attackSamples
                    else -> 1.0
                }
                val sample = sin(twoPiF * (i.toDouble() / sampleRate)) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.8).toInt().toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            audioTrack?.release()
            audioTrack = track
        } catch (_: Exception) {}
    }

    fun stop() {
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (_: Exception) {}
    }
}
