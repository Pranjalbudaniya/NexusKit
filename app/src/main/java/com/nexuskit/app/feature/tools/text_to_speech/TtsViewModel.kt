package com.nexuskit.app.feature.tools.text_to_speech

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class TtsUiState(
    val text: String = "",
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val selectedLanguage: AvailableLanguage? = null
)

@HiltViewModel
class TtsViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val ttsManager = TtsManager(application)
    val ttsState = ttsManager.ttsState
    val availableLanguages = ttsManager.availableLanguages

    private val _uiState = MutableStateFlow(TtsUiState())
    val uiState: StateFlow<TtsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("text_to_speech"))
        }

        viewModelScope.launch {
            availableLanguages.collect { langs ->
                if (_uiState.value.selectedLanguage == null && langs.isNotEmpty()) {
                    val defaultLang = langs.find { it.locale.language == Locale.getDefault().language } ?: langs.first()
                    _uiState.update { it.copy(selectedLanguage = defaultLang) }
                }
            }
        }
    }

    fun onTextChanged(text: String) {
        _uiState.update { it.copy(text = text) }
    }

    fun onSpeechRateChanged(rate: Float) {
        _uiState.update { it.copy(speechRate = rate) }
    }

    fun onPitchChanged(pitch: Float) {
        _uiState.update { it.copy(pitch = pitch) }
    }

    fun onLanguageSelected(lang: AvailableLanguage) {
        _uiState.update { it.copy(selectedLanguage = lang) }
    }

    fun speak() {
        val s = _uiState.value
        ttsManager.speak(s.text, s.speechRate, s.pitch, s.selectedLanguage?.locale)
    }

    fun stop() {
        ttsManager.stop()
    }

    fun resetControls() {
        _uiState.update { it.copy(speechRate = 1.0f, pitch = 1.0f) }
    }

    fun loadSample() {
        val sample = "Hello! This is offline text-to-speech powered entirely by Android's built-in speech engine in NexusKit. No internet required."
        onTextChanged(sample)
    }

    fun clear() {
        _uiState.update { it.copy(text = "") }
        stop()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.release()
    }
}
