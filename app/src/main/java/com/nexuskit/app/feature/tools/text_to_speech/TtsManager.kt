package com.nexuskit.app.feature.tools.text_to_speech

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class TtsState {
    INITIALIZING,
    READY,
    SPEAKING,
    ERROR
}

data class AvailableLanguage(
    val displayName: String,
    val locale: Locale
)

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null

    private val _ttsState = MutableStateFlow(TtsState.INITIALIZING)
    val ttsState: StateFlow<TtsState> = _ttsState.asStateFlow()

    private val _availableLanguages = MutableStateFlow<List<AvailableLanguage>>(emptyList())
    val availableLanguages: StateFlow<List<AvailableLanguage>> = _availableLanguages.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val engine = tts ?: return
            _ttsState.value = TtsState.READY

            // Extract available locales supported by the offline engine
            val supported = runCatching {
                val available = engine.availableLanguages ?: emptySet()
                available.filter { loc ->
                    val availability = engine.isLanguageAvailable(loc)
                    availability >= TextToSpeech.LANG_AVAILABLE
                }.map { loc ->
                    AvailableLanguage(
                        displayName = loc.getDisplayName(Locale.getDefault()).replaceFirstChar { it.uppercase() },
                        locale = loc
                    )
                }.sortedBy { it.displayName }
            }.getOrDefault(emptyList())

            _availableLanguages.value = if (supported.isNotEmpty()) supported else listOf(
                AvailableLanguage(Locale.US.displayName, Locale.US),
                AvailableLanguage(Locale.UK.displayName, Locale.UK)
            )

            engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _ttsState.value = TtsState.SPEAKING
                }

                override fun onDone(utteranceId: String?) {
                    _ttsState.value = TtsState.READY
                }

                override fun onError(utteranceId: String?) {
                    _ttsState.value = TtsState.READY
                }
            })
        } else {
            _ttsState.value = TtsState.ERROR
        }
    }

    fun speak(text: String, rate: Float, pitch: Float, locale: Locale?) {
        val engine = tts ?: return
        if (text.isBlank()) return

        runCatching {
            engine.setSpeechRate(rate)
            engine.setPitch(pitch)
            if (locale != null) {
                engine.language = locale
            }
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "nexus_tts_${System.currentTimeMillis()}")
            }
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, params, "nexus_tts_id")
        }
    }

    fun stop() {
        tts?.stop()
        _ttsState.value = TtsState.READY
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
