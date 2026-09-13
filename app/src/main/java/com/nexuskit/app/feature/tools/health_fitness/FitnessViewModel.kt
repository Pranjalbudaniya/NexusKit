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
    val waterWeightKg: String = "70",
    val waterActivityHours: String = "1",
    val waterIntakeLiters: Double = FitnessEngine.calculateWaterIntake(70.0, 1.0),
    // Macros
    val macroWeightKg: String = "75",
    val macroHeightCm: String = "175",
    val macroAge: String = "25",
    val macroIsMale: Boolean = true,
    val macroGoal: String = "cut",
    val macroResult: MacroResult = FitnessEngine.calculateMacros(75.0, 175.0, 25, true, "cut"),
    // Ideal Weight
    val idealHeightCm: String = "175",
    val idealIsMale: Boolean = true,
    val idealWeightResults: Map<String, Double> = FitnessEngine.calculateIdealWeight(175.0, true),
    // 1RM
    val liftWeightKg: String = "80",
    val liftReps: String = "8",
    val estimated1Rm: Double = FitnessEngine.calculateOneRepMax(80.0, 8),
    // Heart Rate
    val hrAge: String = "28",
    val hrZones: HeartRateZones = FitnessEngine.calculateHeartRateZones(28),
    // Sleep
    val wakeHour: Int = 7,
    val wakeMinute: Int = 0,
    val bedTimes: List<String> = FitnessEngine.calculateSleepCycles(7, 0),
    // Pace
    val paceDistanceKm: String = "5",
    val paceTimeMin: String = "25",
    val calculatedPace: String = "5:00 min/km",
    val calculatedSpeed: String = "12.00 km/h"
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
        val w = weight.toDoubleOrNull() ?: 70.0
        val a = activity.toDoubleOrNull() ?: 0.0
        val intake = FitnessEngine.calculateWaterIntake(w, a)
        _uiState.update { it.copy(waterWeightKg = weight, waterActivityHours = activity, waterIntakeLiters = intake) }
    }

    fun onMacroInputsChanged(weight: String, height: String, age: String, isMale: Boolean, goal: String) {
        val w = weight.toDoubleOrNull() ?: 70.0
        val h = height.toDoubleOrNull() ?: 170.0
        val ag = age.toIntOrNull() ?: 25
        val res = FitnessEngine.calculateMacros(w, h, ag, isMale, goal)
        _uiState.update {
            it.copy(
                macroWeightKg = weight, macroHeightCm = height, macroAge = age,
                macroIsMale = isMale, macroGoal = goal, macroResult = res
            )
        }
    }

    fun onIdealWeightInputsChanged(height: String, isMale: Boolean) {
        val h = height.toDoubleOrNull() ?: 175.0
        val res = FitnessEngine.calculateIdealWeight(h, isMale)
        _uiState.update { it.copy(idealHeightCm = height, idealIsMale = isMale, idealWeightResults = res) }
    }

    fun onOneRepMaxInputsChanged(weight: String, reps: String) {
        val w = weight.toDoubleOrNull() ?: 80.0
        val r = reps.toIntOrNull() ?: 1
        val res = FitnessEngine.calculateOneRepMax(w, r)
        _uiState.update { it.copy(liftWeightKg = weight, liftReps = reps, estimated1Rm = res) }
    }

    fun onHrAgeChanged(age: String) {
        val a = age.toIntOrNull() ?: 25
        val zones = FitnessEngine.calculateHeartRateZones(a)
        _uiState.update { it.copy(hrAge = age, hrZones = zones) }
    }

    fun onWakeTimeChanged(hour: Int, min: Int) {
        val times = FitnessEngine.calculateSleepCycles(hour, min)
        _uiState.update { it.copy(wakeHour = hour, wakeMinute = min, bedTimes = times) }
    }

    fun onPaceInputsChanged(dist: String, time: String) {
        val d = dist.toDoubleOrNull() ?: 5.0
        val t = time.toDoubleOrNull() ?: 25.0
        val (p, s) = FitnessEngine.calculatePace(d, t)
        _uiState.update { it.copy(paceDistanceKm = dist, paceTimeMin = time, calculatedPace = p, calculatedSpeed = s) }
    }
}
