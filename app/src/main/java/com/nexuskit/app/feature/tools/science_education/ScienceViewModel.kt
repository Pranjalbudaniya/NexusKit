package com.nexuskit.app.feature.tools.science_education

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
import javax.inject.Inject

data class ScienceUiState(
    val selectedTab: ScienceTab = ScienceTab.PERIODIC_TABLE,
    val selectedElement: ElementInfo? = null,
    val elementSearchQuery: String = "",
    val constantSearchQuery: String = "",
    // Ohm's law inputs
    val voltageInput: String = "12",
    val currentInput: String = "2",
    val resistanceInput: String = "",
    val powerInput: String = "",
    val ohmsResults: Map<String, Double> = emptyMap(),
    // Trig inputs
    val trigAngleInput: String = "45",
    val isTrigDegrees: Boolean = true,
    val trigResults: Map<String, Double> = ScienceEngine.calculateTrigonometry(45.0, true),
    // Scientific notation inputs
    val decimalInput: String = "0.000042",
    val scientificResult: String = ScienceEngine.toScientificNotation("0.000042"),
    val mantissaInput: String = "4.2",
    val exponentInput: String = "-5",
    val decimalResult: String = ScienceEngine.fromScientificNotation("4.2", "-5")
)

@HiltViewModel
class ScienceViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ScienceUiState())
    val uiState: StateFlow<ScienceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("science_education"))
        }
        recalculateOhms()
    }

    fun onTabSelected(tab: ScienceTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onSelectElement(element: ElementInfo?) {
        _uiState.update { it.copy(selectedElement = element) }
    }

    fun onElementSearchChanged(q: String) {
        _uiState.update { it.copy(elementSearchQuery = q) }
    }

    fun onConstantSearchChanged(q: String) {
        _uiState.update { it.copy(constantSearchQuery = q) }
    }

    fun onVoltageChanged(v: String) {
        _uiState.update { it.copy(voltageInput = v) }
        recalculateOhms()
    }

    fun onCurrentChanged(i: String) {
        _uiState.update { it.copy(currentInput = i) }
        recalculateOhms()
    }

    fun onResistanceChanged(r: String) {
        _uiState.update { it.copy(resistanceInput = r) }
        recalculateOhms()
    }

    fun onPowerChanged(p: String) {
        _uiState.update { it.copy(powerInput = p) }
        recalculateOhms()
    }

    private fun recalculateOhms() {
        val v = _uiState.value.voltageInput.toDoubleOrNull()
        val i = _uiState.value.currentInput.toDoubleOrNull()
        val r = _uiState.value.resistanceInput.toDoubleOrNull()
        val p = _uiState.value.powerInput.toDoubleOrNull()
        val res = ScienceEngine.calculateOhmsLaw(v, i, r, p)
        _uiState.update { it.copy(ohmsResults = res) }
    }

    fun onTrigAngleChanged(angle: String) {
        _uiState.update { it.copy(trigAngleInput = angle) }
        recalculateTrig()
    }

    fun toggleTrigUnit() {
        _uiState.update { it.copy(isTrigDegrees = !it.isTrigDegrees) }
        recalculateTrig()
    }

    private fun recalculateTrig() {
        val a = _uiState.value.trigAngleInput.toDoubleOrNull() ?: 0.0
        val res = ScienceEngine.calculateTrigonometry(a, _uiState.value.isTrigDegrees)
        _uiState.update { it.copy(trigResults = res) }
    }

    fun onDecimalChanged(dec: String) {
        val res = ScienceEngine.toScientificNotation(dec)
        _uiState.update { it.copy(decimalInput = dec, scientificResult = res) }
    }

    fun onMantissaExponentChanged(m: String, e: String) {
        val res = ScienceEngine.fromScientificNotation(m, e)
        _uiState.update { it.copy(mantissaInput = m, exponentInput = e, decimalResult = res) }
    }
}
