package com.nexuskit.app.feature.tools.health_fitness

import kotlin.math.*

enum class FitnessTab {
    WATER, MACROS, IDEAL_WEIGHT, ONE_REP_MAX, CALORIE_BURN, BODY_FAT, PACE, SLEEP_CYCLES, HEART_RATE
}

data class MacroResult(
    val dailyCalories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int
)

data class HeartRateZones(
    val maxHr: Int,
    val zone1Recovery: IntRange, // 50-60%
    val zone2Aerobic: IntRange,  // 60-70%
    val zone3Tempo: IntRange,    // 70-80%
    val zone4Threshold: IntRange,// 80-90%
    val zone5Anaerobic: IntRange // 90-100%
)

object FitnessEngine {

    fun calculateWaterIntake(weightKg: Double, activityHoursPerDay: Double): Double {
        // Base 35ml per kg + 500ml per hour of activity
        val baseLiters = weightKg * 0.035
        val activityLiters = activityHoursPerDay * 0.5
        return baseLiters + activityLiters
    }

    fun calculateMacros(
        weightKg: Double,
        heightCm: Double,
        age: Int,
        isMale: Boolean,
        goal: String // "maintain", "cut", "bulk"
    ): MacroResult {
        // Mifflin-St Jeor BMR
        val bmr = if (isMale) {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5
        } else {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161
        }
        val tdee = bmr * 1.4 // Moderate activity factor
        val targetCalories = when (goal.lowercase()) {
            "cut" -> (tdee - 450).toInt()
            "bulk" -> (tdee + 400).toInt()
            else -> tdee.toInt()
        }

        val proteinG = (weightKg * 2.0).toInt() // 2g per kg
        val fatG = ((targetCalories * 0.25) / 9.0).toInt()
        val remainingCal = targetCalories - (proteinG * 4) - (fatG * 9)
        val carbsG = (remainingCal / 4.0).toInt().coerceAtLeast(50)

        return MacroResult(targetCalories, proteinG, carbsG, fatG)
    }

    fun calculateIdealWeight(heightCm: Double, isMale: Boolean): Map<String, Double> {
        val heightInches = heightCm / 2.54
        val inchesOver5ft = (heightInches - 60.0).coerceAtLeast(0.0)

        val devine = if (isMale) 50.0 + (2.3 * inchesOver5ft) else 45.5 + (2.3 * inchesOver5ft)
        val robinson = if (isMale) 52.0 + (1.9 * inchesOver5ft) else 49.0 + (1.7 * inchesOver5ft)
        val miller = if (isMale) 56.2 + (1.41 * inchesOver5ft) else 53.1 + (1.36 * inchesOver5ft)
        val hamwi = if (isMale) 48.0 + (2.7 * inchesOver5ft) else 45.5 + (2.2 * inchesOver5ft)

        return mapOf(
            "Devine Formula" to devine,
            "Robinson Formula" to robinson,
            "Miller Formula" to miller,
            "Hamwi Formula" to hamwi
        )
    }

    fun calculateOneRepMax(weightLifted: Double, reps: Int): Double {
        if (reps <= 1) return weightLifted
        // Epley Formula: 1RM = weight * (1 + reps / 30)
        return weightLifted * (1.0 + (reps / 30.0))
    }

    fun calculateHeartRateZones(age: Int): HeartRateZones {
        val maxHr = (220 - age).coerceIn(100, 220)
        return HeartRateZones(
            maxHr = maxHr,
            zone1Recovery = (maxHr * 0.50).toInt()..(maxHr * 0.60).toInt(),
            zone2Aerobic = (maxHr * 0.60).toInt()..(maxHr * 0.70).toInt(),
            zone3Tempo = (maxHr * 0.70).toInt()..(maxHr * 0.80).toInt(),
            zone4Threshold = (maxHr * 0.80).toInt()..(maxHr * 0.90).toInt(),
            zone5Anaerobic = (maxHr * 0.90).toInt()..maxHr
        )
    }

    fun calculateSleepCycles(wakeUpHour: Int, wakeUpMinute: Int): List<String> {
        val wakeTotalMinutes = wakeUpHour * 60 + wakeUpMinute
        val cycleMinutes = 90
        val sleepOnsetMinutes = 15

        // Suggest 6, 5, 4, and 3 cycles prior
        val cycles = listOf(6, 5, 4, 3)
        return cycles.map { cycleCount ->
            var bedTotalMinutes = wakeTotalMinutes - (cycleCount * cycleMinutes) - sleepOnsetMinutes
            while (bedTotalMinutes < 0) bedTotalMinutes += 24 * 60
            val h = (bedTotalMinutes / 60) % 24
            val m = bedTotalMinutes % 60
            String.format(java.util.Locale.US, "%02d:%02d (%d cycles, %.1f hrs)", h, m, cycleCount, (cycleCount * 1.5))
        }
    }

    fun calculatePace(distanceKm: Double, timeMinutes: Double): Pair<String, String> {
        if (distanceKm <= 0 || timeMinutes <= 0) return "--" to "--"
        val paceMinPerKm = timeMinutes / distanceKm
        val pMin = paceMinPerKm.toInt()
        val pSec = ((paceMinPerKm - pMin) * 60).toInt()
        val speedKmh = (distanceKm / (timeMinutes / 60.0))

        val paceStr = String.format(java.util.Locale.US, "%d:%02d min/km", pMin, pSec)
        val speedStr = String.format(java.util.Locale.US, "%.2f km/h", speedKmh)
        return paceStr to speedStr
    }
}
