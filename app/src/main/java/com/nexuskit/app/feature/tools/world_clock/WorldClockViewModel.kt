package com.nexuskit.app.feature.tools.world_clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class WorldClockUiState(
    val selectedOffsetHours: Float = 0f, // 0 = now, -12 to +12
    val baseInstant: Instant = Instant.now(),
    val cities: List<CityTimeItem> = WorldClockEngine.getTimesForInstant(Instant.now())
)

@HiltViewModel
class WorldClockViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorldClockUiState())
    val uiState: StateFlow<WorldClockUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("world_clock"))
        }
    }

    fun onOffsetChanged(offsetHours: Float) {
        val secondsToAdd = (offsetHours * 3600).toLong()
        val targetInstant = Instant.now().plusSeconds(secondsToAdd)
        val cities = WorldClockEngine.getTimesForInstant(targetInstant)
        _uiState.update {
            it.copy(
                selectedOffsetHours = offsetHours,
                baseInstant = targetInstant,
                cities = cities
            )
        }
    }

    fun resetToNow() {
        onOffsetChanged(0f)
    }
}
