package com.nexuskit.app.feature.tools.unix_timestamp

import android.app.Application
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UnixTimestampUiState(
    val liveSeconds: Long = System.currentTimeMillis() / 1000L,
    val liveMillis: Long = System.currentTimeMillis(),
    val isLivePaused: Boolean = false,
    val inputTimestamp: String = "",
    val isInputSeconds: Boolean = true,
    val timestampDetails: TimestampDetails? = null,
    val timestampError: String? = null,
    val inputDateString: String = "",
    val convertedEpochSeconds: Long? = null,
    val convertedEpochMillis: Long? = null,
    val dateError: String? = null
)

@HiltViewModel
class UnixTimestampViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UnixTimestampUiState())
    val uiState: StateFlow<UnixTimestampUiState> = _uiState.asStateFlow()

    private var tickingJob: Job? = null

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("unix_timestamp"))
        }
        startTicking()
    }

    private fun startTicking() {
        tickingJob?.cancel()
        tickingJob = viewModelScope.launch {
            while (isActive) {
                if (!_uiState.value.isLivePaused) {
                    val now = System.currentTimeMillis()
                    _uiState.update {
                        it.copy(
                            liveSeconds = now / 1000L,
                            liveMillis = now
                        )
                    }
                }
                delay(1000L)
            }
        }
    }

    fun togglePauseLive() {
        _uiState.update { it.copy(isLivePaused = !it.isLivePaused) }
    }

    fun onInputTimestampChanged(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            _uiState.update { it.copy(inputTimestamp = text, timestampDetails = null, timestampError = null) }
            return
        }

        val parsedVal = trimmed.toLongOrNull()
        if (parsedVal == null) {
            _uiState.update {
                it.copy(
                    inputTimestamp = text,
                    timestampDetails = null,
                    timestampError = "Please enter a valid numeric epoch timestamp"
                )
            }
            return
        }

        // Auto-detect seconds vs millis: if > 100_000_000_000, likely millis (since year 1973 in ms is > 10^11)
        val isSec = if (parsedVal > 100_000_000_000L) false else _uiState.value.isInputSeconds

        val res = UnixTimestampEngine.formatEpoch(parsedVal, isSec)
        res.fold(
            onSuccess = { details ->
                _uiState.update {
                    it.copy(
                        inputTimestamp = text,
                        isInputSeconds = isSec,
                        timestampDetails = details,
                        timestampError = null
                    )
                }
            },
            onFailure = { err ->
                _uiState.update {
                    it.copy(
                        inputTimestamp = text,
                        timestampDetails = null,
                        timestampError = err.localizedMessage ?: "Invalid timestamp"
                    )
                }
            }
        )
    }

    fun setUnit(isSeconds: Boolean) {
        _uiState.update { it.copy(isInputSeconds = isSeconds) }
        onInputTimestampChanged(_uiState.value.inputTimestamp)
    }

    fun applyPreset(preset: TimestampPreset) {
        val epoch = UnixTimestampEngine.getPreset(preset)
        _uiState.update { it.copy(isInputSeconds = true) }
        onInputTimestampChanged(epoch.toString())
    }

    fun addOffset(secondsToAdd: Long) {
        val currentSec = _uiState.value.timestampDetails?.epochSeconds ?: (_uiState.value.liveSeconds)
        val newSec = currentSec + secondsToAdd
        _uiState.update { it.copy(isInputSeconds = true) }
        onInputTimestampChanged(newSec.toString())
    }

    fun onInputDateChanged(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            _uiState.update {
                it.copy(
                    inputDateString = text,
                    convertedEpochSeconds = null,
                    convertedEpochMillis = null,
                    dateError = null
                )
            }
            return
        }

        val res = UnixTimestampEngine.parseDateToEpoch(trimmed)
        res.fold(
            onSuccess = { (sec, ms) ->
                _uiState.update {
                    it.copy(
                        inputDateString = text,
                        convertedEpochSeconds = sec,
                        convertedEpochMillis = ms,
                        dateError = null
                    )
                }
            },
            onFailure = { err ->
                _uiState.update {
                    it.copy(
                        inputDateString = text,
                        convertedEpochSeconds = null,
                        convertedEpochMillis = null,
                        dateError = err.localizedMessage ?: "Invalid date"
                    )
                }
            }
        )
    }

    fun loadSampleDate() {
        onInputDateChanged("2026-10-19 19:30:00")
    }

    override fun onCleared() {
        super.onCleared()
        tickingJob?.cancel()
    }
}
