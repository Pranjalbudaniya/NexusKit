package com.nexuskit.app.feature.tools.gradient_generator

import android.app.Application
import androidx.compose.ui.graphics.Color
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
import javax.inject.Inject
import kotlin.random.Random

data class GradientUiState(
    val gradientType: GradientType = GradientType.LINEAR,
    val colors: List<Color> = GradientEngine.PRESETS[0].colors,
    val angleDegrees: Float = 135f,
    val activePresetName: String? = GradientEngine.PRESETS[0].name
)

@HiltViewModel
class GradientViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GradientUiState())
    val uiState: StateFlow<GradientUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("gradient_generator"))
        }
    }

    fun applyPreset(preset: GradientPreset) {
        _uiState.update {
            it.copy(
                colors = preset.colors,
                angleDegrees = preset.angle,
                activePresetName = preset.name
            )
        }
    }

    fun onGradientTypeChanged(type: GradientType) {
        _uiState.update { it.copy(gradientType = type) }
    }

    fun onAngleChanged(angle: Float) {
        _uiState.update { it.copy(angleDegrees = angle, activePresetName = null) }
    }

    fun updateColor(index: Int, newColor: Color) {
        val current = _uiState.value.colors.toMutableList()
        if (index in current.indices) {
            current[index] = newColor
            _uiState.update { it.copy(colors = current, activePresetName = null) }
        }
    }

    fun addColor(newColor: Color) {
        val current = _uiState.value.colors.toMutableList()
        if (current.size < 5) {
            current.add(newColor)
            _uiState.update { it.copy(colors = current, activePresetName = null) }
        }
    }

    fun removeColor(index: Int) {
        val current = _uiState.value.colors.toMutableList()
        if (current.size > 2 && index in current.indices) {
            current.removeAt(index)
            _uiState.update { it.copy(colors = current, activePresetName = null) }
        }
    }

    fun randomize() {
        val count = Random.nextInt(2, 4)
        val randColors = (1..count).map {
            Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)
        }
        val randAngle = (Random.nextInt(0, 8) * 45).toFloat()
        _uiState.update {
            it.copy(
                colors = randColors,
                angleDegrees = randAngle,
                activePresetName = null
            )
        }
    }
}
