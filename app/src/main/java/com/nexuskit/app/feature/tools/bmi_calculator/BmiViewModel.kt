package com.nexuskit.app.feature.tools.bmi_calculator

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

data class BmiUiState(
    val unitSystem: UnitSystem = UnitSystem.METRIC,
    val gender: Gender = Gender.MALE,
    val age: Int = 25,
    // Metric inputs
    val heightCm: Float = 175f,
    val weightKg: Float = 70f,
    // Imperial inputs
    val heightFeet: Int = 5,
    val heightInches: Int = 9,
    val weightLbs: Float = 154f,
    // Results
    val result: BmiResult? = null
)

@HiltViewModel
class BmiViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BmiUiState())
    val uiState: StateFlow<BmiUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("bmi_calculator"))
        }
        recalculate()
    }

    fun onUnitSystemChanged(system: UnitSystem) {
        _uiState.update { it.copy(unitSystem = system) }
        recalculate()
    }

    fun onGenderChanged(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
        recalculate()
    }

    fun onAgeChanged(age: Int) {
        _uiState.update { it.copy(age = age.coerceIn(5, 120)) }
        recalculate()
    }

    fun onHeightCmChanged(heightCm: Float) {
        _uiState.update { it.copy(heightCm = heightCm) }
        recalculate()
    }

    fun onWeightKgChanged(weightKg: Float) {
        _uiState.update { it.copy(weightKg = weightKg) }
        recalculate()
    }

    fun onHeightImperialChanged(feet: Int, inches: Int) {
        _uiState.update { it.copy(heightFeet = feet, heightInches = inches) }
        recalculate()
    }

    fun onWeightLbsChanged(weightLbs: Float) {
        _uiState.update { it.copy(weightLbs = weightLbs) }
        recalculate()
    }

    private fun recalculate() {
        val state = _uiState.value
        val (effectiveWeightKg, effectiveHeightCm) = if (state.unitSystem == UnitSystem.METRIC) {
            Pair(state.weightKg.toDouble(), state.heightCm.toDouble())
        } else {
            val totalInches = (state.heightFeet * 12) + state.heightInches
            val cm = totalInches * 2.54
            val kg = state.weightLbs * 0.45359237
            Pair(kg, cm)
        }

        val res = BmiEngine.calculate(
            weightKg = effectiveWeightKg,
            heightCm = effectiveHeightCm,
            age = state.age,
            gender = state.gender
        )

        _uiState.update { it.copy(result = res) }
    }
}
