package com.nexuskit.app.feature.tools.calculator

import kotlin.math.abs

/** Maximum digits allowed in the input before further digits are ignored. */
const val CALC_MAX_DIGITS = 12

/** |result| beyond this is treated as overflow → "Error". */
const val CALC_OVERFLOW_THRESHOLD = 1e15

/**
 * The four basic operators. [symbol] is the display character (uses ×/÷/−
 * for visual clarity, not the ASCII * / - used internally anywhere).
 */
enum class CalcOperator(val symbol: String) {
    ADD("+"), SUBTRACT("−"), MULTIPLY("×"), DIVIDE("÷");

    /** Applies this operator to [a] and [b], returning Value or Error. */
    fun apply(a: Double, b: Double): CalcResult = when (this) {
        ADD      -> CalcResult.Value(a + b)
        SUBTRACT -> CalcResult.Value(a - b)
        MULTIPLY -> CalcResult.Value(a * b)
        DIVIDE   -> if (b == 0.0) CalcResult.Error("Cannot divide by 0")
                    else CalcResult.Value(a / b)
    }
}

sealed class CalcResult {
    data class Value(val value: Double) : CalcResult()
    data class Error(val message: String) : CalcResult()
}

/**
 * Formats a Double for calculator display.
 *
 * Rules:
 *   - NaN / Infinite / |value| > CALC_OVERFLOW_THRESHOLD → "Error"
 *   - 0.0 and -0.0 both → "0"  (never displays "-0")
 *   - Whole numbers     → no decimal point ("12", not "12.0")
 *   - Decimals          → trimmed of trailing zeros, max 8 decimal places
 */
fun formatCalcNumber(value: Double): String {
    if (value.isNaN() || value.isInfinite()) return "Error"
    if (abs(value) > CALC_OVERFLOW_THRESHOLD) return "Error"
    if (value == 0.0) return "0"   // normalises -0.0 → "0"

    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        "%.8f".format(value).trimEnd('0').trimEnd('.')
    }
}
