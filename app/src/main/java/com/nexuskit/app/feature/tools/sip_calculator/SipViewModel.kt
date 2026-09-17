package com.nexuskit.app.feature.tools.sip_calculator

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.pow

enum class SipMode(val label: String) {
    SIP("Monthly SIP"),
    LUMPSUM("Lump Sum")
}

data class YearlyBreakdown(
    val year: Int,
    val invested: Double,
    val futureValue: Double,
    val returns: Double
)

data class SipUiState(
    val mode: SipMode = SipMode.SIP,
    val amountInput: String = "5000",
    val expectedReturnRate: Float = 12f,
    val timePeriodYears: Int = 10,
    val currencySymbol: String = "₹",
    val totalInvested: Double = 0.0,
    val estimatedReturns: Double = 0.0,
    val totalValue: Double = 0.0,
    val investedRatio: Float = 0.5f,
    val returnsRatio: Float = 0.5f,
    val breakdown: List<YearlyBreakdown> = emptyList()
)

@HiltViewModel
class SipViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SipUiState())
    val uiState: StateFlow<SipUiState> = _uiState.asStateFlow()

    init {
        recalculate()
    }

    fun onModeSelected(mode: SipMode) {
        _uiState.update { it.copy(mode = mode) }
        recalculate()
    }

    fun onAmountChanged(input: String) {
        val filtered = input.filter { it.isDigit() }.take(10)
        _uiState.update { it.copy(amountInput = filtered) }
        recalculate()
    }

    fun onRateChanged(rate: Float) {
        _uiState.update { it.copy(expectedReturnRate = rate) }
        recalculate()
    }

    fun onPeriodChanged(years: Int) {
        _uiState.update { it.copy(timePeriodYears = years.coerceIn(1, 40)) }
        recalculate()
    }

    fun onCurrencySelected(symbol: String) {
        _uiState.update { it.copy(currencySymbol = symbol) }
    }

    private fun recalculate() {
        val state = _uiState.value
        val amount = state.amountInput.toDoubleOrNull() ?: 0.0
        val rate = state.expectedReturnRate.toDouble()
        val years = state.timePeriodYears

        val breakdownList = mutableListOf<YearlyBreakdown>()

        if (state.mode == SipMode.SIP) {
            val i = rate / 12.0 / 100.0
            val totalMonths = years * 12
            val invested = amount * totalMonths

            val maturity = if (i > 0) {
                amount * (( (1.0 + i).pow(totalMonths.toDouble()) - 1.0) / i) * (1.0 + i)
            } else {
                invested
            }
            val returns = (maturity - invested).coerceAtLeast(0.0)

            for (y in 1..years) {
                val m = y * 12
                val invY = amount * m
                val valY = if (i > 0) {
                    amount * (( (1.0 + i).pow(m.toDouble()) - 1.0) / i) * (1.0 + i)
                } else invY
                breakdownList.add(
                    YearlyBreakdown(
                        year = y,
                        invested = invY,
                        futureValue = valY,
                        returns = (valY - invY).coerceAtLeast(0.0)
                    )
                )
            }

            val total = (invested + returns).coerceAtLeast(1.0)
            val invRatio = (invested / total).toFloat().coerceIn(0f, 1f)

            _uiState.update {
                it.copy(
                    totalInvested = invested,
                    estimatedReturns = returns,
                    totalValue = maturity,
                    investedRatio = invRatio,
                    returnsRatio = 1f - invRatio,
                    breakdown = breakdownList
                )
            }
        } else {
            // LUMPSUM
            val invested = amount
            val maturity = amount * (1.0 + rate / 100.0).pow(years.toDouble())
            val returns = (maturity - invested).coerceAtLeast(0.0)

            for (y in 1..years) {
                val valY = amount * (1.0 + rate / 100.0).pow(y.toDouble())
                breakdownList.add(
                    YearlyBreakdown(
                        year = y,
                        invested = invested,
                        futureValue = valY,
                        returns = (valY - invested).coerceAtLeast(0.0)
                    )
                )
            }

            val total = (invested + returns).coerceAtLeast(1.0)
            val invRatio = (invested / total).toFloat().coerceIn(0f, 1f)

            _uiState.update {
                it.copy(
                    totalInvested = invested,
                    estimatedReturns = returns,
                    totalValue = maturity,
                    investedRatio = invRatio,
                    returnsRatio = 1f - invRatio,
                    breakdown = breakdownList
                )
            }
        }
    }

    companion object {
        private val formatter = DecimalFormat("#,##,##0")
        fun formatAmount(value: Double): String = formatter.format(value.toLong().coerceAtLeast(0))
    }
}
