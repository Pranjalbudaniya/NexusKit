package com.nexuskit.app.feature.tools.unix_timestamp

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class TimestampDetails(
    val epochSeconds: Long,
    val epochMillis: Long,
    val isoUtc: String,
    val rfc2822Utc: String,
    val localFormatted: String,
    val localTimeZoneName: String,
    val relativeTime: String,
    val dayOfYear: Int,
    val isLeapYear: Boolean
)

object UnixTimestampEngine {

    fun formatEpoch(epochInput: Long, isSeconds: Boolean): Result<TimestampDetails> = runCatching {
        val millis = if (isSeconds) epochInput * 1000L else epochInput
        val seconds = if (isSeconds) epochInput else epochInput / 1000L

        val date = Date(millis)
        val cal = Calendar.getInstance().apply { time = date }

        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val rfcFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val localFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy  hh:mm:ss a (zzz)", Locale.getDefault())

        val nowMillis = System.currentTimeMillis()
        val diffSec = (millis - nowMillis) / 1000L
        val relative = formatRelativeTime(diffSec)

        val year = cal.get(Calendar.YEAR)
        val isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

        TimestampDetails(
            epochSeconds = seconds,
            epochMillis = millis,
            isoUtc = isoFormat.format(date),
            rfc2822Utc = rfcFormat.format(date),
            localFormatted = localFormat.format(date),
            localTimeZoneName = TimeZone.getDefault().displayName,
            relativeTime = relative,
            dayOfYear = cal.get(Calendar.DAY_OF_YEAR),
            isLeapYear = isLeap
        )
    }

    fun parseDateToEpoch(dateStr: String): Result<Pair<Long, Long>> = runCatching {
        val trimmed = dateStr.trim()
        val formats = listOf(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd",
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy",
            "MM/dd/yyyy HH:mm:ss",
            "MM/dd/yyyy"
        )

        var parsedDate: Date? = null
        for (pattern in formats) {
            runCatching {
                val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                    isLenient = false
                    if (pattern.endsWith("'Z'")) {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                }
                parsedDate = sdf.parse(trimmed)
            }
            if (parsedDate != null) break
        }

        val date = parsedDate ?: throw IllegalArgumentException("Unrecognized date format. Try YYYY-MM-DD HH:mm:ss")
        val millis = date.time
        val seconds = millis / 1000L
        Pair(seconds, millis)
    }

    fun getPreset(preset: TimestampPreset): Long {
        val cal = Calendar.getInstance()
        return when (preset) {
            TimestampPreset.NOW -> System.currentTimeMillis() / 1000L
            TimestampPreset.START_OF_TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis / 1000L
            }
            TimestampPreset.END_OF_TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                cal.timeInMillis / 1000L
            }
            TimestampPreset.START_OF_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.timeInMillis / 1000L
            }
            TimestampPreset.START_OF_YEAR -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.timeInMillis / 1000L
            }
        }
    }

    private fun formatRelativeTime(diffSeconds: Long): String {
        val isPast = diffSeconds <= 0
        val absSec = Math.abs(diffSeconds)
        val text = when {
            absSec < 5 -> return "Just now"
            absSec < 60 -> "${absSec} seconds"
            absSec < 3600 -> "${absSec / 60} minute(s)"
            absSec < 86400 -> "${absSec / 3600} hour(s)"
            absSec < 2592000 -> "${absSec / 86400} day(s)"
            absSec < 31536000 -> "${absSec / 2592000} month(s)"
            else -> "${absSec / 31536000} year(s)"
        }
        return if (isPast) "$text ago" else "in $text"
    }
}

enum class TimestampPreset(val label: String) {
    NOW("Now"),
    START_OF_TODAY("Start of Today"),
    END_OF_TODAY("End of Today"),
    START_OF_MONTH("Start of Month"),
    START_OF_YEAR("Start of Year")
}
