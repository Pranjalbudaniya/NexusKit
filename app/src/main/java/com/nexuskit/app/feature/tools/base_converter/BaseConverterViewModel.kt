package com.nexuskit.app.feature.tools.base_converter

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

data class BaseConverterUiState(
    val activeBase: NumberBase = NumberBase.DEC,
    val inputValue: String = "42",
    val hexValue: String = "2A",
    val decValue: String = "42",
    val octValue: String = "52",
    val binValue: String = "0010 1010",
    val bitLength: Int = 6,
    val ascii: String = "'*'"
)

@HiltViewModel
class BaseConverterViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BaseConverterUiState())
    val uiState: StateFlow<BaseConverterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("base_converter"))
        }
        recalculate()
    }

    fun onActiveBaseSelected(base: NumberBase) {
        val currentHex = _uiState.value.hexValue
        val newInitial = when (base) {
            NumberBase.HEX -> _uiState.value.hexValue
            NumberBase.DEC -> _uiState.value.decValue
            NumberBase.OCT -> _uiState.value.octValue
            NumberBase.BIN -> _uiState.value.binValue.replace(" ", "")
        }
        _uiState.update {
            it.copy(
                activeBase = base,
                inputValue = newInitial
            )
        }
        recalculate()
    }

    fun onKeyPressed(key: String) {
        val current = _uiState.value.inputValue
        val updated = if (current == "0") key else current + key
        _uiState.update { it.copy(inputValue = updated) }
        recalculate()
    }

    fun onBackspace() {
        val current = _uiState.value.inputValue
        val updated = if (current.length <= 1) "0" else current.dropLast(1)
        _uiState.update { it.copy(inputValue = updated) }
        recalculate()
    }

    fun onClear() {
        _uiState.update { it.copy(inputValue = "0") }
        recalculate()
    }

    private fun recalculate() {
        val state = _uiState.value
        val result = BaseConverterEngine.convertFrom(state.inputValue, state.activeBase)
        if (result != null) {
            _uiState.update {
                it.copy(
                    hexValue = result.hex,
                    decValue = result.dec,
                    octValue = result.oct,
                    binValue = result.bin,
                    bitLength = result.bitLength,
                    ascii = result.asciiRepresentation
                )
            }
        }
    }
}
