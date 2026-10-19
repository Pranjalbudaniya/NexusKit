package com.nexuskit.app.feature.tools.sound_meter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SoundMeterUiState(
    val isRecording: Boolean = false,
    val currentDb: Float = 0f,
    val minDb: Float = 0f,
    val maxDb: Float = 0f,
    val avgDb: Float = 0f,
    val category: String = "Idle",
    val isHarmful: Boolean = false,
    val history: List<Float> = emptyList(),
    val calibrationOffset: Float = 0f
)

@HiltViewModel
class SoundMeterViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SoundMeterUiState())
    val uiState: StateFlow<SoundMeterUiState> = _uiState.asStateFlow()

    private var audioJob: Job? = null
    private var totalSamples = 0
    private var sumDb = 0.0

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("sound_meter"))
        }
    }

    fun startListening() {
        if (audioJob?.isActive == true) return
        _uiState.update { it.copy(isRecording = true) }

        audioJob = viewModelScope.launch {
            SoundMeterEngine.startAudioStream(_uiState.value.calibrationOffset).collect { reading ->
                val db = reading.currentDb
                totalSamples++
                sumDb += db
                val avg = (sumDb / totalSamples).toFloat()

                _uiState.update { current ->
                    val min = if (current.minDb == 0f) db else kotlin.math.min(current.minDb, db)
                    val max = kotlin.math.max(current.maxDb, db)
                    val updatedHistory = (current.history + db).takeLast(30)

                    current.copy(
                        currentDb = db,
                        minDb = min,
                        maxDb = max,
                        avgDb = avg,
                        category = reading.category,
                        isHarmful = reading.isHarmful,
                        history = updatedHistory
                    )
                }
            }
        }
    }

    fun pauseListening() {
        audioJob?.cancel()
        audioJob = null
        _uiState.update { it.copy(isRecording = false) }
    }

    fun resetStats() {
        totalSamples = 0
        sumDb = 0.0
        _uiState.update {
            it.copy(
                currentDb = 0f,
                minDb = 0f,
                maxDb = 0f,
                avgDb = 0f,
                category = "Idle",
                isHarmful = false,
                history = emptyList()
            )
        }
    }

    fun onCalibrationChanged(offset: Float) {
        _uiState.update { it.copy(calibrationOffset = offset) }
        if (_uiState.value.isRecording) {
            pauseListening()
            startListening()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioJob?.cancel()
    }
}
