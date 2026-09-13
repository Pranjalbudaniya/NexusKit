package com.nexuskit.app.feature.tools.screen_light

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScreenLightUiState(
    val selectedMode: LightMode = LightMode.SCREEN,
    val screenColor: Color = Color.White,
    val brightnessFraction: Float = 1f,
    val isTorchOn: Boolean = false,
    val strobeFrequencyHz: Float = 5f,
    val isStrobeRunning: Boolean = false,
    val isSosRunning: Boolean = false,
    val isScreenFlashOn: Boolean = true,
    // ── Morse Code Transmitter State ──────────────────────────────────────
    val morseInputText: String = "HELLO WORLD",
    val morseSymbols: List<MorseSymbol> = ScreenLightEngine.textToMorseSymbols("HELLO WORLD"),
    val activeSymbolIndex: Int = -1,
    val isMorseTransmitting: Boolean = false,
    val isMorseLooping: Boolean = false,
    val morseWpm: Int = 12,
    val morseTorchEnabled: Boolean = true,
    val morseScreenEnabled: Boolean = true,
    val morseAudioEnabled: Boolean = true,
    val morseHapticEnabled: Boolean = false
)

@HiltViewModel
class ScreenLightViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ScreenLightUiState())
    val uiState: StateFlow<ScreenLightUiState> = _uiState.asStateFlow()

    private var flashJob: Job? = null
    private var morseJob: Job? = null
    private val morseAudioPlayer = MorseAudioPlayer()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("screen_light"))
        }
    }

    fun onModeSelected(mode: LightMode) {
        stopAllFlashing()
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun onScreenColorSelected(color: Color) {
        _uiState.update { it.copy(screenColor = color) }
    }

    fun onBrightnessChanged(fraction: Float) {
        _uiState.update { it.copy(brightnessFraction = fraction.coerceIn(0.1f, 1f)) }
    }

    fun toggleFlashlight() {
        val next = !_uiState.value.isTorchOn
        val success = ScreenLightEngine.toggleCameraFlashlight(getApplication(), next)
        if (success) {
            _uiState.update { it.copy(isTorchOn = next) }
        }
    }

    fun onStrobeFrequencyChanged(freq: Float) {
        _uiState.update { it.copy(strobeFrequencyHz = freq) }
        if (_uiState.value.isStrobeRunning) {
            startStrobe()
        }
    }

    fun toggleStrobe() {
        if (_uiState.value.isStrobeRunning) {
            stopAllFlashing()
        } else {
            startStrobe()
        }
    }

    private fun startStrobe() {
        stopAllFlashing()
        _uiState.update { it.copy(isStrobeRunning = true) }
        val periodMs = (1000L / _uiState.value.strobeFrequencyHz.toLong()).coerceAtLeast(50L)
        flashJob = viewModelScope.launch {
            var on = false
            while (true) {
                on = !on
                _uiState.update { it.copy(isScreenFlashOn = on) }
                ScreenLightEngine.toggleCameraFlashlight(getApplication(), on)
                delay(periodMs / 2)
            }
        }
    }

    fun toggleSos() {
        if (_uiState.value.isSosRunning) {
            stopAllFlashing()
        } else {
            startSos()
        }
    }

    private fun startSos() {
        stopAllFlashing()
        _uiState.update { it.copy(isSosRunning = true) }
        // SOS in Morse: ... --- ...
        val dot = 200L
        val dash = 600L
        val pause = 200L
        val charPause = 600L
        val wordPause = 1400L

        val pattern = listOf(
            dot, pause, dot, pause, dot, charPause, // S
            dash, pause, dash, pause, dash, charPause, // O
            dot, pause, dot, pause, dot, wordPause // S
        )

        flashJob = viewModelScope.launch {
            while (true) {
                for (i in pattern.indices step 2) {
                    val duration = pattern[i]
                    val gap = if (i + 1 < pattern.size) pattern[i + 1] else 200L

                    _uiState.update { it.copy(isScreenFlashOn = true) }
                    ScreenLightEngine.toggleCameraFlashlight(getApplication(), true)
                    delay(duration)

                    _uiState.update { it.copy(isScreenFlashOn = false) }
                    ScreenLightEngine.toggleCameraFlashlight(getApplication(), false)
                    delay(gap)
                }
            }
        }
    }

    // ── Morse Code Transmitter Functions ──────────────────────────────────────

    fun onMorseInputChanged(newText: String) {
        val filtered = newText.take(120)
        _uiState.update {
            it.copy(
                morseInputText = filtered,
                morseSymbols = ScreenLightEngine.textToMorseSymbols(filtered)
            )
        }
        if (_uiState.value.isMorseTransmitting) {
            startMorseTransmission()
        }
    }

    fun setMorsePreset(presetText: String) {
        onMorseInputChanged(presetText)
    }

    fun onMorseWpmChanged(wpm: Int) {
        _uiState.update { it.copy(morseWpm = wpm.coerceIn(5, 35)) }
    }

    fun toggleMorseLoop() {
        _uiState.update { it.copy(isMorseLooping = !it.isMorseLooping) }
    }

    fun toggleMorseTorch() {
        _uiState.update { it.copy(morseTorchEnabled = !it.morseTorchEnabled) }
    }

    fun toggleMorseScreen() {
        _uiState.update { it.copy(morseScreenEnabled = !it.morseScreenEnabled) }
    }

    fun toggleMorseAudio() {
        _uiState.update { it.copy(morseAudioEnabled = !it.morseAudioEnabled) }
    }

    fun toggleMorseHaptic() {
        _uiState.update { it.copy(morseHapticEnabled = !it.morseHapticEnabled) }
    }

    fun toggleMorseTransmission() {
        if (_uiState.value.isMorseTransmitting) {
            stopMorseTransmission()
        } else {
            startMorseTransmission()
        }
    }

    private fun startMorseTransmission() {
        stopAllFlashing()
        val text = _uiState.value.morseInputText.trim()
        if (text.isEmpty()) return

        _uiState.update { it.copy(isMorseTransmitting = true, activeSymbolIndex = 0) }

        morseJob = viewModelScope.launch {
            val app = getApplication<Application>()
            do {
                val symbols = _uiState.value.morseSymbols
                val dotDuration = ScreenLightEngine.wpmToDotDurationMs(_uiState.value.morseWpm)
                val dashDuration = dotDuration * 3
                val elementPause = dotDuration
                val letterPause = dotDuration * 3
                val wordPause = dotDuration * 7

                for (idx in symbols.indices) {
                    if (!_uiState.value.isMorseTransmitting) break
                    _uiState.update { it.copy(activeSymbolIndex = idx) }
                    val symbol = symbols[idx]

                    if (symbol.char == ' ' || symbol.morse == "/") {
                        delay(wordPause)
                        continue
                    }

                    // Transmit each dot / dash in the symbol
                    for (dotDash in symbol.morse) {
                        if (!_uiState.value.isMorseTransmitting) break
                        val duration = if (dotDash == '-') dashDuration else dotDuration

                        // Turn ON outputs
                        if (_uiState.value.morseScreenEnabled) {
                            _uiState.update { it.copy(isScreenFlashOn = true) }
                        }
                        if (_uiState.value.morseTorchEnabled) {
                            ScreenLightEngine.toggleCameraFlashlight(app, true)
                        }
                        if (_uiState.value.morseAudioEnabled) {
                            morseAudioPlayer.playTone(duration)
                        }
                        if (_uiState.value.morseHapticEnabled) {
                            ScreenLightEngine.vibrateDevice(app, duration)
                        }

                        delay(duration)

                        // Turn OFF outputs
                        _uiState.update { it.copy(isScreenFlashOn = false) }
                        if (_uiState.value.morseTorchEnabled) {
                            ScreenLightEngine.toggleCameraFlashlight(app, false)
                        }

                        delay(elementPause)
                    }

                    // Pause between characters
                    delay(letterPause)
                }

                _uiState.update { it.copy(activeSymbolIndex = -1) }
                if (_uiState.value.isMorseLooping) {
                    delay(wordPause)
                }
            } while (_uiState.value.isMorseLooping && _uiState.value.isMorseTransmitting)

            stopMorseTransmission()
        }
    }

    private fun stopMorseTransmission() {
        morseJob?.cancel()
        morseJob = null
        morseAudioPlayer.stop()
        ScreenLightEngine.toggleCameraFlashlight(getApplication(), false)
        _uiState.update {
            it.copy(
                isMorseTransmitting = false,
                activeSymbolIndex = -1,
                isScreenFlashOn = true
            )
        }
    }

    private fun stopAllFlashing() {
        flashJob?.cancel()
        flashJob = null
        stopMorseTransmission()
        ScreenLightEngine.toggleCameraFlashlight(getApplication(), false)
        _uiState.update {
            it.copy(
                isTorchOn = false,
                isStrobeRunning = false,
                isSosRunning = false,
                isMorseTransmitting = false,
                isScreenFlashOn = true
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAllFlashing()
    }
}
