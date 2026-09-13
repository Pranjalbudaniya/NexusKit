package com.nexuskit.app.feature.tools.calculator

import com.nexuskit.app.core.base.BaseViewModel
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ── UI State ─────────────────────────────────────────────────────────────────

/**
 * [previousValue]      the left operand, resolved after the most recent operator.
 * [pendingOperator]     the operator waiting for its right operand.
 * [currentInput]        the number currently being typed / the result shown.
 * [awaitingNewOperand]  true right after an operator or "=" — the next digit
 *                       press REPLACES currentInput rather than appending.
 * [displayExpression]   small preview line, e.g. "12 +" or "12 + 5 =".
 * [isError]             true after divide-by-zero or overflow. Only AC,
 *                       a digit, or decimal can clear this state.
 */
data class CalculatorUiState(
    val previousValue: Double? = null,
    val pendingOperator: CalcOperator? = null,
    val currentInput: String = "0",
    val awaitingNewOperand: Boolean = false,
    val displayExpression: String = "",
    val isError: Boolean = false
) {
    val displayText: String get() = if (isError) "Error" else currentInput
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    trackToolOpenedUseCase: TrackToolOpenedUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        // Rule 7 (Developer Guide): every tool ViewModel tracks its own opens.
        launchSafe { trackToolOpenedUseCase(TrackToolParams("calculator")) }
    }

    // ── Digit / Decimal ─────────────────────────────────────────────────────

    fun onDigit(digit: String) = _uiState.update { s ->
        when {
            s.isError -> CalculatorUiState(currentInput = digit)
            s.awaitingNewOperand -> s.copy(
                currentInput = if (digit == "0") "0" else digit,
                awaitingNewOperand = false
            )
            s.currentInput == "0" && digit == "0" -> s
            s.currentInput == "0" -> s.copy(currentInput = digit)
            s.currentInput.length < CALC_MAX_DIGITS ->
                s.copy(currentInput = s.currentInput + digit)
            else -> s
        }
    }

    fun onDecimal() = _uiState.update { s ->
        when {
            s.isError -> CalculatorUiState(currentInput = "0.")
            s.awaitingNewOperand -> s.copy(currentInput = "0.", awaitingNewOperand = false)
            !s.currentInput.contains(".") -> s.copy(currentInput = "${s.currentInput}.")
            else -> s
        }
    }

    // ── Operators ────────────────────────────────────────────────────────────

    fun onOperator(op: CalcOperator) = _uiState.update { s ->
        if (s.isError) return@update s

        val pendingOp = s.pendingOperator
        val current = s.currentInput.toDoubleOrNull() ?: 0.0

        // User is swapping the operator before typing the second operand —
        // e.g. pressed "+" then changed their mind to "−". Just swap it.
        if (s.awaitingNewOperand && pendingOp != null) {
            return@update s.copy(
                pendingOperator = op,
                displayExpression = "${formatCalcNumber(s.previousValue ?: 0.0)} ${op.symbol}"
            )
        }

        if (pendingOp != null) {
            // Chain: resolve the pending operation first (left-to-right).
            when (val result = pendingOp.apply(s.previousValue ?: 0.0, current)) {
                is CalcResult.Error -> CalculatorUiState(isError = true)
                is CalcResult.Value -> s.copy(
                    previousValue = result.value,
                    currentInput = formatCalcNumber(result.value),
                    pendingOperator = op,
                    displayExpression = "${formatCalcNumber(result.value)} ${op.symbol}",
                    awaitingNewOperand = true
                )
            }
        } else {
            s.copy(
                previousValue = current,
                pendingOperator = op,
                displayExpression = "${formatCalcNumber(current)} ${op.symbol}",
                awaitingNewOperand = true
            )
        }
    }

    // ── Equals ───────────────────────────────────────────────────────────────

    fun onEquals() = _uiState.update { s ->
        if (s.isError) return@update s
        val op   = s.pendingOperator ?: return@update s   // nothing to compute
        val prev = s.previousValue   ?: return@update s
        val current = s.currentInput.toDoubleOrNull() ?: 0.0

        when (val result = op.apply(prev, current)) {
            is CalcResult.Error -> CalculatorUiState(isError = true)
            is CalcResult.Value -> CalculatorUiState(
                currentInput = formatCalcNumber(result.value),
                displayExpression =
                    "${formatCalcNumber(prev)} ${op.symbol} ${formatCalcNumber(current)} =",
                awaitingNewOperand = true
            )
        }
    }

    // ── AC / Backspace / ± / % ──────────────────────────────────────────────

    fun onClear() {
        _uiState.value = CalculatorUiState()
    }

    fun onBackspace() = _uiState.update { s ->
        when {
            s.isError -> CalculatorUiState()
            s.awaitingNewOperand -> s   // nothing typed yet for this operand
            s.currentInput.length > 1 -> s.copy(currentInput = s.currentInput.dropLast(1))
            else -> s.copy(currentInput = "0")
        }
    }

    fun onToggleSign() = _uiState.update { s ->
        if (s.isError) return@update s
        val value = s.currentInput.toDoubleOrNull() ?: return@update s
        if (value == 0.0) return@update s   // no "-0"
        s.copy(currentInput = formatCalcNumber(-value))
    }

    fun onPercent() = _uiState.update { s ->
        if (s.isError) return@update s
        val value = s.currentInput.toDoubleOrNull() ?: return@update s
        s.copy(currentInput = formatCalcNumber(value / 100.0))
    }

    override fun handleException(throwable: Throwable) {
        _uiState.value = CalculatorUiState(isError = true)
    }
}
