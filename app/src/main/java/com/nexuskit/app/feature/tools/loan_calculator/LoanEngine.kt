package com.nexuskit.app.feature.tools.loan_calculator

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.pow

data class LoanResult(
    val monthlyEmi: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val principalRatio: Float,
    val interestRatio: Float
)

object LoanEngine {

    private val df = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))

    fun calculateEmi(
        principal: Double,
        annualRatePercent: Double,
        tenureMonths: Int
    ): LoanResult {
        if (principal <= 0.0 || tenureMonths <= 0) {
            return LoanResult(0.0, 0.0, 0.0, 1f, 0f)
        }

        if (annualRatePercent <= 0.0) {
            val emi = principal / tenureMonths
            return LoanResult(emi, 0.0, principal, 1f, 0f)
        }

        // Monthly interest rate r = R / (12 * 100)
        val r = annualRatePercent / (12.0 * 100.0)
        val n = tenureMonths.toDouble()

        // EMI = [P * r * (1 + r)^n] / [(1 + r)^n - 1]
        val numerator = principal * r * (1.0 + r).pow(n)
        val denominator = (1.0 + r).pow(n) - 1.0

        val emi = numerator / denominator
        val totalPayment = emi * n
        val totalInterest = totalPayment - principal

        val principalRatio = (principal / totalPayment).toFloat().coerceIn(0f, 1f)
        val interestRatio = (totalInterest / totalPayment).toFloat().coerceIn(0f, 1f)

        return LoanResult(
            monthlyEmi = emi,
            totalInterest = totalInterest,
            totalPayment = totalPayment,
            principalRatio = principalRatio,
            interestRatio = interestRatio
        )
    }

    fun format(value: Double): String = synchronized(this) { df.format(value) }
}
