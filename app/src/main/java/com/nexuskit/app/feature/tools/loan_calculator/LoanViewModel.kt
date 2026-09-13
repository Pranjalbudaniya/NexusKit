package com.nexuskit.app.feature.tools.loan_calculator

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

data class LoanUiState(
    val principalStr: String = "25000",
    val annualRatePercent: Float = 7.5f,
    val tenureYears: Int = 3,
    val currencySymbol: String = "$",
    val result: LoanResult = LoanEngine.calculateEmi(25000.0, 7.5, 36)
)

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanUiState())
    val uiState: StateFlow<LoanUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("loan_calculator"))
        }
    }

    fun onPrincipalChanged(principalStr: String) {
        _uiState.update { it.copy(principalStr = principalStr) }
        recalculate()
    }

    fun onRateChanged(rate: Float) {
        _uiState.update { it.copy(annualRatePercent = rate) }
        recalculate()
    }

    fun onTenureYearsChanged(years: Int) {
        _uiState.update { it.copy(tenureYears = years) }
        recalculate()
    }

    fun onCurrencySelected(symbol: String) {
        _uiState.update { it.copy(currencySymbol = symbol) }
    }

    private fun recalculate() {
        val s = _uiState.value
        val principal = s.principalStr.toDoubleOrNull() ?: 0.0
        val tenureMonths = s.tenureYears * 12
        val res = LoanEngine.calculateEmi(principal, s.annualRatePercent.toDouble(), tenureMonths)
        _uiState.update { it.copy(result = res) }
    }
}
