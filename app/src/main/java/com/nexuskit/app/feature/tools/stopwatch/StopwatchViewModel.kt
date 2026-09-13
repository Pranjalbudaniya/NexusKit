package com.nexuskit.app.feature.tools.stopwatch

import androidx.lifecycle.ViewModel
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

enum class TimeToolMode(val displayName: String) {
    STOPWATCH("Stopwatch"),
    TIMER("Countdown Timer")
}

data class LapItem(
    val lapNumber: Int,
    val lapTimeMillis: Long,
    val totalTimeMillis: Long,
    val formattedLapTime: String,
    val formattedTotalTime: String,
    val isFastest: Boolean = false,
    val isSlowest: Boolean = false
)

data class StopwatchUiState(
    val selectedMode: TimeToolMode = TimeToolMode.STOPWATCH,
    // Stopwatch
    val isStopwatchRunning: Boolean = false,
    val stopwatchElapsedMillis: Long = 0L,
    val laps: List<LapItem> = emptyList(),
    // Timer
    val isTimerRunning: Boolean = false,
    val timerTotalMillis: Long = 5 * 60 * 1000L, // 5 min default
    val timerRemainingMillis: Long = 5 * 60 * 1000L,
    val isTimerFinished: Boolean = false
)

@HiltViewModel
class StopwatchViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StopwatchUiState())
    val uiState: StateFlow<StopwatchUiState> = _uiState.asStateFlow()

    private var stopwatchJob: Job? = null
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("stopwatch"))
        }
    }

    fun onModeSelected(mode: TimeToolMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    // ── STOPWATCH METHODS ─────────────────────────────────────────────────────

    fun toggleStopwatch() {
        if (_uiState.value.isStopwatchRunning) {
            pauseStopwatch()
        } else {
            startStopwatch()
        }
    }

    private fun startStopwatch() {
        _uiState.update { it.copy(isStopwatchRunning = true) }
        val startTime = System.currentTimeMillis() - _uiState.value.stopwatchElapsedMillis
        stopwatchJob = viewModelScope.launch {
            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                _uiState.update { it.copy(stopwatchElapsedMillis = elapsed) }
                delay(16) // ~60fps refresh
            }
        }
    }

    private fun pauseStopwatch() {
        stopwatchJob?.cancel()
        _uiState.update { it.copy(isStopwatchRunning = false) }
    }

    fun resetStopwatch() {
        stopwatchJob?.cancel()
        _uiState.update {
            it.copy(
                isStopwatchRunning = false,
                stopwatchElapsedMillis = 0L,
                laps = emptyList()
            )
        }
    }

    fun recordLap() {
        val state = _uiState.value
        val total = state.stopwatchElapsedMillis
        val lastTotal = state.laps.firstOrNull()?.totalTimeMillis ?: 0L
        val lapTime = total - lastTotal

        val newLap = LapItem(
            lapNumber = state.laps.size + 1,
            lapTimeMillis = lapTime,
            totalTimeMillis = total,
            formattedLapTime = formatDuration(lapTime, includeMillis = true),
            formattedTotalTime = formatDuration(total, includeMillis = true)
        )

        val updatedLaps = listOf(newLap) + state.laps
        // Compute fastest & slowest if >= 2 laps
        val lapsWithHighlight = if (updatedLaps.size >= 2) {
            val minTime = updatedLaps.minOf { it.lapTimeMillis }
            val maxTime = updatedLaps.maxOf { it.lapTimeMillis }
            updatedLaps.map {
                it.copy(
                    isFastest = it.lapTimeMillis == minTime,
                    isSlowest = it.lapTimeMillis == maxTime
                )
            }
        } else {
            updatedLaps
        }

        _uiState.update { it.copy(laps = lapsWithHighlight) }
    }

    // ── COUNTDOWN TIMER METHODS ───────────────────────────────────────────────

    fun setTimerPreset(minutes: Int) {
        timerJob?.cancel()
        val millis = minutes * 60 * 1000L
        _uiState.update {
            it.copy(
                timerTotalMillis = millis,
                timerRemainingMillis = millis,
                isTimerRunning = false,
                isTimerFinished = false
            )
        }
    }

    fun toggleTimer() {
        if (_uiState.value.isTimerRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        val state = _uiState.value
        if (state.timerRemainingMillis <= 0L) {
            _uiState.update { it.copy(timerRemainingMillis = it.timerTotalMillis, isTimerFinished = false) }
        }
        _uiState.update { it.copy(isTimerRunning = true, isTimerFinished = false) }

        val targetEnd = System.currentTimeMillis() + _uiState.value.timerRemainingMillis
        timerJob = viewModelScope.launch {
            while (true) {
                val remaining = targetEnd - System.currentTimeMillis()
                if (remaining <= 0L) {
                    _uiState.update {
                        it.copy(
                            timerRemainingMillis = 0L,
                            isTimerRunning = false,
                            isTimerFinished = true
                        )
                    }
                    break
                } else {
                    _uiState.update { it.copy(timerRemainingMillis = remaining) }
                    delay(30)
                }
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = false) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                isTimerRunning = false,
                timerRemainingMillis = it.timerTotalMillis,
                isTimerFinished = false
            )
        }
    }

    companion object {
        fun formatDuration(millis: Long, includeMillis: Boolean = false): String {
            val totalSeconds = millis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val hundredths = (millis % 1000) / 10

            return if (includeMillis) {
                String.format(java.util.Locale.US, "%02d:%02d.%02d", minutes, seconds, hundredths)
            } else {
                String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
            }
        }
    }
}
