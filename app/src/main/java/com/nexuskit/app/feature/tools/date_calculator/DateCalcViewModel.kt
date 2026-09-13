package com.nexuskit.app.feature.tools.date_calculator

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
import java.time.LocalDate
import javax.inject.Inject

enum class DateToolTab { AGE, DIFFERENCE, ADD_SUBTRACT }

data class DateCalcUiState(
    val selectedTab: DateToolTab = DateToolTab.AGE,
    // Age Calculator
    val birthDate: LocalDate = LocalDate.of(2000, 1, 1),
    val ageResult: AgeResult? = null,
    // Difference
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusMonths(6),
    val differenceResult: DateDifferenceResult? = null,
    // Add / Subtract
    val baseDate: LocalDate = LocalDate.now(),
    val yearsDelta: Long = 0,
    val monthsDelta: Long = 1,
    val daysDelta: Long = 10,
    val isAdd: Boolean = true,
    val calculatedDate: LocalDate = LocalDate.now().plusMonths(1).plusDays(10)
)

@HiltViewModel
class DateCalcViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DateCalcUiState())
    val uiState: StateFlow<DateCalcUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("date_calculator"))
        }
        recalculateAge()
        recalculateDifference()
        recalculateAddSubtract()
    }

    fun onTabSelected(tab: DateToolTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onBirthDateChanged(date: LocalDate) {
        _uiState.update { it.copy(birthDate = date) }
        recalculateAge()
    }

    fun onStartDateChanged(date: LocalDate) {
        _uiState.update { it.copy(startDate = date) }
        recalculateDifference()
    }

    fun onEndDateChanged(date: LocalDate) {
        _uiState.update { it.copy(endDate = date) }
        recalculateDifference()
    }

    fun onBaseDateChanged(date: LocalDate) {
        _uiState.update { it.copy(baseDate = date) }
        recalculateAddSubtract()
    }

    fun onDeltasChanged(years: Long, months: Long, days: Long, isAdd: Boolean) {
        _uiState.update {
            it.copy(
                yearsDelta = years,
                monthsDelta = months,
                daysDelta = days,
                isAdd = isAdd
            )
        }
        recalculateAddSubtract()
    }

    private fun recalculateAge() {
        val res = DateCalcEngine.calculateAge(_uiState.value.birthDate)
        _uiState.update { it.copy(ageResult = res) }
    }

    private fun recalculateDifference() {
        val s = _uiState.value
        val res = DateCalcEngine.calculateDifference(s.startDate, s.endDate)
        _uiState.update { it.copy(differenceResult = res) }
    }

    private fun recalculateAddSubtract() {
        val s = _uiState.value
        val res = DateCalcEngine.addSubtractDate(s.baseDate, s.yearsDelta, s.monthsDelta, s.daysDelta, s.isAdd)
        _uiState.update { it.copy(calculatedDate = res) }
    }
}
