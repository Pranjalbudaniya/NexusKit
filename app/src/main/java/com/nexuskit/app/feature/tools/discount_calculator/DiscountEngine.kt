package com.nexuskit.app.feature.tools.discount_calculator

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class DiscountResult(
    val originalPrice: Double,
    val primaryDiscountAmount: Double,
    val priceAfterPrimary: Double,
    val additionalDiscountAmount: Double,
    val priceAfterDiscounts: Double,
    val taxAmount: Double,
    val finalPrice: Double,
    val totalSavings: Double,
    val effectiveDiscountPercent: Double
)

object DiscountEngine {

    private val df = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))

    fun calculate(
        originalPrice: Double,
        discountPercent: Double,
        additionalDiscountPercent: Double = 0.0,
        taxPercent: Double = 0.0
    ): DiscountResult {
        if (originalPrice <= 0.0) {
            return DiscountResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }

        val primaryDiscount = (discountPercent / 100.0) * originalPrice
        val afterPrimary = (originalPrice - primaryDiscount).coerceAtLeast(0.0)

        val additionalDiscount = (additionalDiscountPercent / 100.0) * afterPrimary
        val afterDiscounts = (afterPrimary - additionalDiscount).coerceAtLeast(0.0)

        val tax = (taxPercent / 100.0) * afterDiscounts
        val finalPrice = afterDiscounts + tax

        val savings = (originalPrice - afterDiscounts).coerceAtLeast(0.0)
        val effectivePercent = if (originalPrice > 0.0) (savings / originalPrice) * 100.0 else 0.0

        return DiscountResult(
            originalPrice = originalPrice,
            primaryDiscountAmount = primaryDiscount,
            priceAfterPrimary = afterPrimary,
            additionalDiscountAmount = additionalDiscount,
            priceAfterDiscounts = afterDiscounts,
            taxAmount = tax,
            finalPrice = finalPrice,
            totalSavings = savings,
            effectiveDiscountPercent = effectivePercent
        )
    }

    fun format(value: Double): String = synchronized(this) { df.format(value) }
}
