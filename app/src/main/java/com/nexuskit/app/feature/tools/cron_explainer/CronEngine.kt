package com.nexuskit.app.feature.tools.cron_explainer

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CronExplanationResult(
    val expression: String,
    val humanDescription: String,
    val nextRuns: List<Date>,
    val fieldsSummary: Map<String, String>
)

object CronEngine {

    private val MONTH_NAMES = listOf(
        "", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    private val DAY_OF_WEEK_NAMES = listOf(
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    )

    fun explain(cronExpr: String): Result<CronExplanationResult> = runCatching {
        val parts = cronExpr.trim().split("\\s+".toRegex())
        if (parts.size != 5) {
            throw IllegalArgumentException("Expected 5 fields (minute hour day-of-month month day-of-week). Found ${parts.size}.")
        }

        val minPart = parts[0]
        val hourPart = parts[1]
        val domPart = parts[2]
        val monthPart = parts[3]
        val dowPart = parts[4]

        val desc = buildDescription(minPart, hourPart, domPart, monthPart, dowPart)
        val nextRuns = calculateNextRuns(parts, 5)

        val summary = mapOf(
            "Minute" to describeField(minPart, "minute", 0, 59),
            "Hour" to describeField(hourPart, "hour", 0, 23),
            "Day of Month" to describeField(domPart, "day", 1, 31),
            "Month" to describeMonthField(monthPart),
            "Day of Week" to describeDowField(dowPart)
        )

        CronExplanationResult(
            expression = cronExpr.trim(),
            humanDescription = desc,
            nextRuns = nextRuns,
            fieldsSummary = summary
        )
    }

    private fun buildDescription(min: String, hour: String, dom: String, month: String, dow: String): String {
        val timeDesc = when {
            min == "*" && hour == "*" -> "Every minute"
            min.startsWith("*/") && hour == "*" -> "Every ${min.removePrefix("*/")} minutes"
            min == "0" && hour == "*" -> "At the start of every hour"
            min != "*" && hour == "*" -> "At minute $min of every hour"
            min.startsWith("*/") && hour != "*" -> "Every ${min.removePrefix("*/")} minutes past hour $hour"
            min != "*" && hour != "*" -> "At ${formatTime(hour, min)}"
            else -> "At minute $min, hour $hour"
        }

        val domDesc = when {
            dom == "*" -> ""
            dom.startsWith("*/") -> " on every ${dom.removePrefix("*/")} days"
            dom.contains(",") -> " on days $dom of the month"
            else -> " on day $dom of the month"
        }

        val monthDesc = when {
            month == "*" -> ""
            month.startsWith("*/") -> " in every ${month.removePrefix("*/")} months"
            else -> " in ${describeMonthField(month)}"
        }

        val dowDesc = when {
            dow == "*" || dow == "?" -> ""
            dow == "1-5" -> " (Monday through Friday)"
            dow == "0,6" || dow == "6,0" || dow == "7,6" || dow == "6,7" -> " (on weekends)"
            else -> " on ${describeDowField(dow)}"
        }

        return "$timeDesc$domDesc$monthDesc$dowDesc".trim()
    }

    private fun formatTime(hour: String, min: String): String {
        val h = hour.toIntOrNull()
        val m = min.toIntOrNull()
        if (h != null && m != null && h in 0..23 && m in 0..59) {
            val ampm = if (h < 12) "AM" else "PM"
            val displayH = when {
                h == 0 -> 12
                h > 12 -> h - 12
                else -> h
            }
            val displayM = String.format(Locale.US, "%02d", m)
            return "$displayH:$displayM $ampm"
        }
        return "$hour:$min"
    }

    private fun describeField(part: String, unit: String, minVal: Int, maxVal: Int): String {
        return when {
            part == "*" -> "Every $unit"
            part.startsWith("*/") -> "Every ${part.removePrefix("*/")} ${unit}s"
            part.contains("-") -> "Between ${part.split("-")[0]} and ${part.split("-")[1]}"
            part.contains(",") -> "At $part"
            else -> "At $part"
        }
    }

    private fun describeMonthField(part: String): String {
        if (part == "*") return "Every month"
        if (part.startsWith("*/")) return "Every ${part.removePrefix("*/")} months"
        val num = part.toIntOrNull()
        if (num != null && num in 1..12) return MONTH_NAMES[num]
        return part
    }

    private fun describeDowField(part: String): String {
        if (part == "*" || part == "?") return "Every day of week"
        val num = part.toIntOrNull()
        if (num != null && num in 0..7) return DAY_OF_WEEK_NAMES[num]
        return part
    }

    fun calculateNextRuns(parts: List<String>, count: Int): List<Date> {
        val nextRuns = mutableListOf<Date>()
        val cal = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, 1) // start from next minute
        }

        var iterations = 0
        while (nextRuns.size < count && iterations < 500000) {
            iterations++
            val m = cal.get(Calendar.MINUTE)
            val h = cal.get(Calendar.HOUR_OF_DAY)
            val dom = cal.get(Calendar.DAY_OF_MONTH)
            val mon = cal.get(Calendar.MONTH) + 1
            val dow = cal.get(Calendar.DAY_OF_WEEK) - 1 // Calendar.SUNDAY=1 -> 0

            if (matchesField(parts[0], m, 0, 59) &&
                matchesField(parts[1], h, 0, 23) &&
                matchesField(parts[2], dom, 1, 31) &&
                matchesField(parts[3], mon, 1, 12) &&
                matchesDow(parts[4], dow)
            ) {
                nextRuns.add(cal.time)
            }
            cal.add(Calendar.MINUTE, 1)
        }

        return nextRuns
    }

    private fun matchesField(field: String, value: Int, min: Int, max: Int): Boolean {
        if (field == "*" || field == "?") return true
        if (field.startsWith("*/")) {
            val step = field.removePrefix("*/").toIntOrNull() ?: return false
            return step > 0 && (value % step == 0)
        }
        if (field.contains(",")) {
            return field.split(",").any { matchesField(it.trim(), value, min, max) }
        }
        if (field.contains("-")) {
            val bounds = field.split("-")
            if (bounds.size == 2) {
                val start = bounds[0].toIntOrNull() ?: return false
                val end = bounds[1].toIntOrNull() ?: return false
                return value in start..end
            }
        }
        val target = field.toIntOrNull() ?: return false
        return value == target
    }

    private fun matchesDow(field: String, dow0Indexed: Int): Boolean {
        if (field == "*" || field == "?") return true
        // In cron, 0 and 7 are both Sunday
        val checkVal = if (dow0Indexed == 0) 7 else dow0Indexed
        return matchesField(field, dow0Indexed, 0, 7) || matchesField(field, checkVal, 0, 7)
    }
}
