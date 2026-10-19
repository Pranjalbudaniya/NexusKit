package com.nexuskit.app.feature.tools.health_fitness

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

data class FitnessUiState(
    val selectedTab: FitnessTab = FitnessTab.WATER,
    // Water
    val waterWeightKg: String = "",
    val waterActivityHours: String = "",
    val waterIntakeLiters: Double = 0.0,
    // Macros
    val macroWeightKg: String = "",
    val macroHeightCm: String = "",
    val macroAge: String = "",
    val macroIsMale: Boolean = true,
    val macroGoal: String = "cut",
    val macroResult: MacroResult = MacroResult(0, 0, 0, 0),
    // Ideal Weight
    val idealHeightCm: String = "",
    val idealIsMale: Boolean = true,
    val idealWeightResults: Map<String, Double> = emptyMap(),
    // 1RM
    val liftWeightKg: String = "",
    val liftReps: String = "",
    val estimated1Rm: Double = 0.0,
    // Heart Rate
    val hrAge: String = "",
    val hrZones: HeartRateZones = HeartRateZones(0, 0..0, 0..0, 0..0, 0..0, 0..0),
    // Sleep
    val wakeHour: Int = 7,
    val wakeMinute: Int = 0,
    val bedTimes: List<String> = FitnessEngine.calculateSleepCycles(7, 0),
    // Pace
    val paceDistanceKm: String = "",
    val paceTimeMin: String = "",
    val calculatedPace: String = "",
    val calculatedSpeed: String = ""
)

@HiltViewModel
class FitnessViewModel @Inject constructor(
    application: Application,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(FitnessUiState())
    val uiState: StateFlow<FitnessUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("health_fitness"))
        }
    }

    fun onTabSelected(tab: FitnessTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onWaterInputsChanged(weight: String, activity: String) {
        val w = weight.toDoubleOrNull()
        val a = activity.toDoubleOrNull() ?: 0.0
        val intake = if (w != null && w > 0) FitnessEngine.calculateWaterIntake(w, a) else 0.0
        _uiState.update { it.copy(waterWeightKg = weight, waterActivityHours = activity, waterIntakeLiters = intake) }
    }

    fun onMacroInputsChanged(weight: String, height: String, age: String, isMale: Boolean, goal: String) {
        val w = weight.toDoubleOrNull()
        val h = height.toDoubleOrNull()
        val ag = age.toIntOrNull()
        val res = if (w != null && h != null && ag != null) {
            FitnessEngine.calculateMacros(w, h, ag, isMale, goal)
        } else {
            MacroResult(0, 0, 0, 0)
        }
        _uiState.update {
            it.copy(
                macroWeightKg = weight, macroHeightCm = height, macroAge = age,
                macroIsMale = isMale, macroGoal = goal, macroResult = res
            )
        }
    }

    fun onIdealWeightInputsChanged(height: String, isMale: Boolean) {
        val h = height.toDoubleOrNull()
        val res = if (h != null) FitnessEngine.calculateIdealWeight(h, isMale) else emptyMap()
        _uiState.update { it.copy(idealHeightCm = height, idealIsMale = isMale, idealWeightResults = res) }
    }

    fun onOneRepMaxInputsChanged(weight: String, reps: String) {
        val w = weight.toDoubleOrNull()
        val r = reps.toIntOrNull()
        val res = if (w != null && r != null) FitnessEngine.calculateOneRepMax(w, r) else 0.0
        _uiState.update { it.copy(liftWeightKg = weight, liftReps = reps, estimated1Rm = res) }
    }

    fun onHrAgeChanged(age: String) {
        val a = age.toIntOrNull()
        val zones = if (a != null) FitnessEngine.calculateHeartRateZones(a) else HeartRateZones(0, 0..0, 0..0, 0..0, 0..0, 0..0)
        _uiState.update { it.copy(hrAge = age, hrZones = zones) }
    }

    fun onWakeTimeChanged(hour: Int, min: Int) {
        val times = FitnessEngine.calculateSleepCycles(hour, min)
        _uiState.update { it.copy(wakeHour = hour, wakeMinute = min, bedTimes = times) }
    }

    fun onPaceInputsChanged(dist: String, time: String) {
        val d = dist.toDoubleOrNull()
        val t = time.toDoubleOrNull()
        val (p, s) = if (d != null && t != null && t > 0) FitnessEngine.calculatePace(d, t) else ("" to "")
        _uiState.update { it.copy(paceDistanceKm = dist, paceTimeMin = time, calculatedPace = p, calculatedSpeed = s) }
    }
}
