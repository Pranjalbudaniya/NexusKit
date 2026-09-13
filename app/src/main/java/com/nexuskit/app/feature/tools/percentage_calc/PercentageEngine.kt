package com.nexuskit.app.feature.tools.percentage_calc

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object PercentageEngine {

    private val symbols = DecimalFormatSymbols(Locale.US)
    private val df = DecimalFormat("#,##0.##", symbols)
    private val currencyDf = DecimalFormat("#,##0.00", symbols)

    fun calculatePercentOf(percent: Double, total: Double): Double {
        return (percent / 100.0) * total
    }

    fun calculateWhatPercentOf(value: Double, total: Double): Double {
        if (total == 0.0) return 0.0
        return (value / total) * 100.0
    }

    fun calculatePercentChange(from: Double, to: Double): Double {
        if (from == 0.0) return 0.0
        return ((to - from) / kotlin.math.abs(from)) * 100.0
    }

    data class TipResult(
        val tipAmount: Double,
        val totalAmount: Double,
        val perPersonAmount: Double,
        val perPersonTip: Double
    )

    fun calculateTip(bill: Double, tipPercent: Double, splitPeople: Int): TipResult {
        val tip = (tipPercent / 100.0) * bill
        val total = bill + tip
        val count = if (splitPeople > 0) splitPeople else 1
        return TipResult(
            tipAmount = tip,
            totalAmount = total,
            perPersonAmount = total / count,
            perPersonTip = tip / count
        )
    }

    fun formatNumber(value: Double): String = synchronized(this) { df.format(value) }
    fun formatCurrency(value: Double): String = synchronized(this) { currencyDf.format(value) }
}
