package com.nexuskit.app.feature.tools.color_palette

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
import javax.inject.Inject

data class ColorPaletteUiState(
    val red: Int = 99,
    val green: Int = 102,
    val blue: Int = 241,
    val hexString: String = "#6366F1",
    val swatches: List<ColorSwatch> = ColorPaletteEngine.generateHarmonies(99, 102, 241),
    val contrast: ContrastReport = ColorPaletteEngine.calculateContrast(99, 102, 241)
)

@HiltViewModel
class ColorPaletteViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColorPaletteUiState())
    val uiState: StateFlow<ColorPaletteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("color_palette"))
        }
    }

    fun onRgbChanged(r: Int, g: Int, b: Int) {
        val hex = ColorPaletteEngine.hexFromRgb(r, g, b)
        val swatches = ColorPaletteEngine.generateHarmonies(r, g, b)
        val contrast = ColorPaletteEngine.calculateContrast(r, g, b)
        _uiState.update {
            it.copy(
                red = r,
                green = g,
                blue = b,
                hexString = hex,
                swatches = swatches,
                contrast = contrast
            )
        }
    }

    fun onHexChanged(hex: String) {
        _uiState.update { it.copy(hexString = hex) }
        val parsed = ColorPaletteEngine.parseHex(hex)
        if (parsed != null) {
            val r = (parsed.red * 255).toInt()
            val g = (parsed.green * 255).toInt()
            val b = (parsed.blue * 255).toInt()
            onRgbChanged(r, g, b)
        }
    }
}
