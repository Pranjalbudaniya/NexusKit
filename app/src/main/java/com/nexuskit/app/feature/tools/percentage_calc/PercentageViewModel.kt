package com.nexuskit.app.feature.tools.percentage_calc

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

enum class PercentMode(val displayName: String) {
    PERCENT_OF("X% of Y"),
    WHAT_PERCENT("What % is X of Y?"),
    CHANGE("% Change"),
    TIP_SPLIT("Tip & Split Bill")
}

data class PercentageUiState(
    val selectedMode: PercentMode = PercentMode.PERCENT_OF,
    // Mode 1: X% of Y
    val mode1Percent: String = "15",
    val mode1Total: String = "200",
    val mode1Result: String = "30",
    // Mode 2: What % is X of Y?
    val mode2Value: String = "25",
    val mode2Total: String = "100",
    val mode2Result: String = "25%",
    // Mode 3: % Change
    val mode3From: String = "80",
    val mode3To: String = "100",
    val mode3Result: String = "+25%",
    val isIncrease: Boolean = true,
    // Mode 4: Tip & Split
    val tipBill: String = "50",
    val tipPercent: String = "15",
    val tipSplitCount: String = "2",
    val tipAmount: String = "$7.50",
    val totalAmount: String = "$57.50",
    val perPersonAmount: String = "$28.75"
)

@HiltViewModel
class PercentageViewModel @Inject constructor(
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PercentageUiState())
    val uiState: StateFlow<PercentageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trackToolOpenedUseCase(TrackToolParams("percentage_calc"))
        }
        recalculate()
    }

    fun onModeSelected(mode: PercentMode) {
        _uiState.update { it.copy(selectedMode = mode) }
        recalculate()
    }

    fun onMode1Changed(percent: String, total: String) {
        _uiState.update { it.copy(mode1Percent = percent, mode1Total = total) }
        val p = percent.toDoubleOrNull() ?: 0.0
        val t = total.toDoubleOrNull() ?: 0.0
        val res = PercentageEngine.calculatePercentOf(p, t)
        _uiState.update { it.copy(mode1Result = PercentageEngine.formatNumber(res)) }
    }

    fun onMode2Changed(value: String, total: String) {
        _uiState.update { it.copy(mode2Value = value, mode2Total = total) }
        val v = value.toDoubleOrNull() ?: 0.0
        val t = total.toDoubleOrNull() ?: 0.0
        val res = PercentageEngine.calculateWhatPercentOf(v, t)
        _uiState.update { it.copy(mode2Result = "${PercentageEngine.formatNumber(res)}%") }
    }

    fun onMode3Changed(from: String, to: String) {
        _uiState.update { it.copy(mode3From = from, mode3To = to) }
        val f = from.toDoubleOrNull() ?: 0.0
        val t = to.toDoubleOrNull() ?: 0.0
        val res = PercentageEngine.calculatePercentChange(f, t)
        val sign = if (res >= 0) "+" else ""
        _uiState.update {
            it.copy(
                mode3Result = "$sign${PercentageEngine.formatNumber(res)}%",
                isIncrease = res >= 0
            )
        }
    }

    fun onTipChanged(bill: String, percent: String, split: String) {
        _uiState.update {
            it.copy(
                tipBill = bill,
                tipPercent = percent,
                tipSplitCount = split
            )
        }
        val b = bill.toDoubleOrNull() ?: 0.0
        val p = percent.toDoubleOrNull() ?: 0.0
        val s = split.toIntOrNull() ?: 1
        val tipRes = PercentageEngine.calculateTip(b, p, s)
        _uiState.update {
            it.copy(
                tipAmount = "$${PercentageEngine.formatCurrency(tipRes.tipAmount)}",
                totalAmount = "$${PercentageEngine.formatCurrency(tipRes.totalAmount)}",
                perPersonAmount = "$${PercentageEngine.formatCurrency(tipRes.perPersonAmount)}"
            )
        }
    }

    private fun recalculate() {
        val s = _uiState.value
        onMode1Changed(s.mode1Percent, s.mode1Total)
        onMode2Changed(s.mode2Value, s.mode2Total)
        onMode3Changed(s.mode3From, s.mode3To)
        onTipChanged(s.tipBill, s.tipPercent, s.tipSplitCount)
    }
}
