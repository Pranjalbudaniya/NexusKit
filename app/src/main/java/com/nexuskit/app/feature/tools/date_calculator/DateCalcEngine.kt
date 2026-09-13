package com.nexuskit.app.feature.tools.date_calculator

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

data class AgeResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalDays: Long,
    val totalWeeks: Long,
    val totalMonths: Long,
    val daysUntilNextBirthday: Long,
    val nextBirthdayDayOfWeek: String
)

data class DateDifferenceResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalDays: Long,
    val totalWeeks: Long,
    val totalHours: Long
)

object DateCalcEngine {

    fun calculateAge(birthDate: LocalDate, today: LocalDate = LocalDate.now()): AgeResult? {
        if (birthDate.isAfter(today)) return null

        val period = Period.between(birthDate, today)
        val totalDays = ChronoUnit.DAYS.between(birthDate, today)
        val totalWeeks = totalDays / 7
        val totalMonths = ChronoUnit.MONTHS.between(birthDate, today)

        // Next birthday
        var nextBday = birthDate.withYear(today.year)
        if (nextBday.isBefore(today) || nextBday.isEqual(today)) {
            nextBday = nextBday.plusYears(1)
        }
        val daysUntilBday = ChronoUnit.DAYS.between(today, nextBday)

        return AgeResult(
            years = period.years,
            months = period.months,
            days = period.days,
            totalDays = totalDays,
            totalWeeks = totalWeeks,
            totalMonths = totalMonths,
            daysUntilNextBirthday = daysUntilBday,
            nextBirthdayDayOfWeek = nextBday.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        )
    }

    fun calculateDifference(start: LocalDate, end: LocalDate): DateDifferenceResult {
        val (earlier, later) = if (start.isBefore(end)) Pair(start, end) else Pair(end, start)
        val period = Period.between(earlier, later)
        val totalDays = ChronoUnit.DAYS.between(earlier, later)

        return DateDifferenceResult(
            years = period.years,
            months = period.months,
            days = period.days,
            totalDays = totalDays,
            totalWeeks = totalDays / 7,
            totalHours = totalDays * 24
        )
    }

    fun addSubtractDate(start: LocalDate, years: Long, months: Long, days: Long, isAdd: Boolean): LocalDate {
        return if (isAdd) {
            start.plusYears(years).plusMonths(months).plusDays(days)
        } else {
            start.minusYears(years).minusMonths(months).minusDays(days)
        }
    }
}
