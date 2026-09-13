package com.nexuskit.app.feature.tools.bmi_calculator

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class Gender { MALE, FEMALE }
enum class UnitSystem { METRIC, IMPERIAL }

enum class BmiCategory(val label: String, val colorHex: Long) {
    VERY_SEVERELY_UNDERWEIGHT("Very Severely Underweight", 0xFF2196F3),
    UNDERWEIGHT("Underweight", 0xFF03A9F4),
    NORMAL("Normal weight", 0xFF4CAF50),
    OVERWEIGHT("Overweight", 0xFFFF9800),
    OBESE_CLASS_1("Obese (Class I)", 0xFFFF5722),
    OBESE_CLASS_2("Severely Obese (Class II)", 0xFFE91E63),
    OBESE_CLASS_3("Very Severely Obese (Class III)", 0xFFD32F2F)
}

enum class ActivityLevel(val label: String, val multiplier: Double) {
    SEDENTARY("Sedentary (Little or no exercise)", 1.2),
    LIGHT("Lightly active (Exercise 1-3 days/week)", 1.375),
    MODERATE("Moderately active (Exercise 3-5 days/week)", 1.55),
    VERY_ACTIVE("Very active (Exercise 6-7 days/week)", 1.725),
    EXTRA_ACTIVE("Extra active (Hard daily exercise / physical job)", 1.9)
}

data class BmiResult(
    val bmi: Double,
    val category: BmiCategory,
    val minHealthyWeightKg: Double,
    val maxHealthyWeightKg: Double,
    val bmrCalories: Double,
    val dailyCaloriesMap: Map<ActivityLevel, Int>
)

object BmiEngine {

    private val df = DecimalFormat("#,##0.#", DecimalFormatSymbols(Locale.US))

    fun calculate(
        weightKg: Double,
        heightCm: Double,
        age: Int,
        gender: Gender
    ): BmiResult? {
        if (weightKg <= 0.0 || heightCm <= 0.0 || age <= 0) return null

        val heightMeters = heightCm / 100.0
        val bmi = weightKg / (heightMeters * heightMeters)

        val category = when {
            bmi < 16.0 -> BmiCategory.VERY_SEVERELY_UNDERWEIGHT
            bmi < 18.5 -> BmiCategory.UNDERWEIGHT
            bmi < 25.0 -> BmiCategory.NORMAL
            bmi < 30.0 -> BmiCategory.OVERWEIGHT
            bmi < 35.0 -> BmiCategory.OBESE_CLASS_1
            bmi < 40.0 -> BmiCategory.OBESE_CLASS_2
            else -> BmiCategory.OBESE_CLASS_3
        }

        // Healthy weight range for BMI 18.5 - 24.9
        val minHealthyKg = 18.5 * (heightMeters * heightMeters)
        val maxHealthyKg = 24.9 * (heightMeters * heightMeters)

        // Mifflin-St Jeor Equation for BMR:
        // Men: (10 × weight in kg) + (6.25 × height in cm) - (5 × age in years) + 5
        // Women: (10 × weight in kg) + (6.25 × height in cm) - (5 × age in years) - 161
        val genderOffset = if (gender == Gender.MALE) 5.0 else -161.0
        val bmr = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age) + genderOffset

        val calories = ActivityLevel.entries.associateWith { (bmr * it.multiplier).toInt() }

        return BmiResult(
            bmi = bmi,
            category = category,
            minHealthyWeightKg = minHealthyKg,
            maxHealthyWeightKg = maxHealthyKg,
            bmrCalories = bmr,
            dailyCaloriesMap = calories
        )
    }

    fun formatNumber(value: Double): String = synchronized(this) { df.format(value) }
}
