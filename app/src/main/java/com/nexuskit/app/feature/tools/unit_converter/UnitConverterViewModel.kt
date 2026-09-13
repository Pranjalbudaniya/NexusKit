package com.nexuskit.app.feature.tools.unit_converter

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

data class UnitConversionItem(
    val unit: ConversionUnit,
    val formattedValue: String
)

data class UnitConverterUiState(
    val selectedCategory: UnitCategory = UnitCategory.LENGTH,
    val fromUnit: ConversionUnit = UnitConverterEngine.allUnits[UnitCategory.LENGTH]!![0],
    val toUnit: ConversionUnit = UnitConverterEngine.allUnits[UnitCategory.LENGTH]!![1],
    val inputValue: String = "1",
    val resultValue: String = "0.001",
    val allCategoryConversions: List<UnitConversionItem> = emptyList()
)

@HiltViewModel
class UnitConverterViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UnitConverterUiState())
    val uiState: StateFlow<UnitConverterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("unit_converter"))
        }
        recalculate()
    }

    fun onCategorySelected(category: UnitCategory) {
        val units = UnitConverterEngine.allUnits[category] ?: return
        val from = units[0]
        val to = if (units.size > 1) units[1] else units[0]
        _uiState.update {
            it.copy(
                selectedCategory = category,
                fromUnit = from,
                toUnit = to
            )
        }
        recalculate()
    }

    fun onFromUnitSelected(unit: ConversionUnit) {
        _uiState.update { it.copy(fromUnit = unit) }
        recalculate()
    }

    fun onToUnitSelected(unit: ConversionUnit) {
        _uiState.update { it.copy(toUnit = unit) }
        recalculate()
    }

    fun onSwapUnits() {
        _uiState.update {
            it.copy(
                fromUnit = it.toUnit,
                toUnit = it.fromUnit
            )
        }
        recalculate()
    }

    fun onInputValueChanged(input: String) {
        // Sanitize input to allow only valid numbers with at most one dot
        val sanitized = input.filter { it.isDigit() || it == '.' || it == '-' }
        if (sanitized.count { it == '.' } > 1) return
        if (sanitized.count { it == '-' } > 1) return

        _uiState.update { it.copy(inputValue = sanitized) }
        recalculate()
    }

    private fun recalculate() {
        val state = _uiState.value
        val num = state.inputValue.toDoubleOrNull() ?: 0.0
        val converted = UnitConverterEngine.convert(num, state.fromUnit, state.toUnit)
        val formattedResult = UnitConverterEngine.formatResult(converted)

        val units = UnitConverterEngine.allUnits[state.selectedCategory] ?: emptyList()
        val allConversions = units.map { u ->
            val valForUnit = UnitConverterEngine.convert(num, state.fromUnit, u)
            UnitConversionItem(u, UnitConverterEngine.formatResult(valForUnit))
        }

        _uiState.update {
            it.copy(
                resultValue = formattedResult,
                allCategoryConversions = allConversions
            )
        }
    }
}
