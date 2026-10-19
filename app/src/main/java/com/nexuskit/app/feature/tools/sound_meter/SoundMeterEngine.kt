package com.nexuskit.app.feature.tools.sound_meter

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlin.math.log10
import kotlin.math.sqrt

data class SoundReading(
    val currentDb: Float,
    val category: String,
    val isHarmful: Boolean
)

object SoundMeterEngine {

    private const val SAMPLE_RATE = 44100
    private const val CHANNEL = AudioFormat.CHANNEL_IN_MONO
    private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT

    fun getCategory(db: Float): Pair<String, Boolean> {
        return when {
            db < 30f -> "Faint (Whisper, Rustling leaves)" to false
            db < 50f -> "Quiet (Quiet library, Bedroom)" to false
            db < 70f -> "Moderate (Normal conversation, Office)" to false
            db < 85f -> "Loud (Busy traffic, Restaurant)" to false
            db < 100f -> "Very Loud (Lawnmower, Power tools)" to true
            else -> "Dangerous (Rock concert, Siren - Hearing damage risk)" to true
        }
    }

    @SuppressLint("MissingPermission")
    fun startAudioStream(calibrationOffset: Float = 0f): Flow<SoundReading> = flow {
        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING)
        val bufferSize = minBufferSize.coerceAtLeast(2048)

        var audioRecord: AudioRecord? = null
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL,
                ENCODING,
                bufferSize
            )

            if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                return@flow
            }

            audioRecord.startRecording()
            val buffer = ShortArray(1024)

            while (currentCoroutineContext().isActive) {
                val readCount = audioRecord.read(buffer, 0, buffer.size)
                if (readCount > 0) {
                    var sumSquare = 0.0
                    for (i in 0 until readCount) {
                        val sample = buffer[i].toDouble()
                        sumSquare += sample * sample
                    }
                    val rms = sqrt(sumSquare / readCount)
                    val rawDb = if (rms > 0.0) 20.0 * log10(rms) else 0.0

                    // Map 16-bit PCM RMS (~1 to 32767) to standard decibel range ~20dB to 120dB
                    val normalizedDb = ((rawDb - 20.0) * 1.4 + 30.0 + calibrationOffset)
                        .coerceIn(0.0, 130.0)
                        .toFloat()

                    val (category, isHarmful) = getCategory(normalizedDb)
                    emit(SoundReading(normalizedDb, category, isHarmful))
                }
                kotlinx.coroutines.delay(100) // 10 updates per second
            }
        } finally {
            try {
                audioRecord?.stop()
                audioRecord?.release()
            } catch (_: Exception) {}
        }
    }.flowOn(Dispatchers.Default)
}
