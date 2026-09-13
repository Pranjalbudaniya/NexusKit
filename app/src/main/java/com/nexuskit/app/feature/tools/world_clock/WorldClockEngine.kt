package com.nexuskit.app.feature.tools.world_clock

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class CityTimeItem(
    val city: String,
    val country: String,
    val timezoneId: String,
    val gmtOffset: String,
    val timeFormatted: String,
    val dateFormatted: String,
    val isDaytime: Boolean
)

object WorldClockEngine {

    val defaultCities = listOf(
        Triple("New York", "United States", "America/New_York"),
        Triple("London", "United Kingdom", "Europe/London"),
        Triple("Paris", "France", "Europe/Paris"),
        Triple("Berlin", "Germany", "Europe/Berlin"),
        Triple("Dubai", "United Arab Emirates", "Asia/Dubai"),
        Triple("Mumbai", "India", "Asia/Kolkata"),
        Triple("Singapore", "Singapore", "Asia/Singapore"),
        Triple("Tokyo", "Japan", "Asia/Tokyo"),
        Triple("Sydney", "Australia", "Australia/Sydney"),
        Triple("Los Angeles", "United States", "America/Los_Angeles")
    )

    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    private val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd")

    fun getTimesForInstant(instant: Instant): List<CityTimeItem> {
        return defaultCities.map { (city, country, zoneStr) ->
            val zoneId = ZoneId.of(zoneStr)
            val zdt = ZonedDateTime.ofInstant(instant, zoneId)

            val offset = zdt.offset.toString()
            val gmtFormatted = if (offset == "Z") "GMT+0" else "GMT$offset"

            val hour = zdt.hour
            val isDay = hour in 6..18

            CityTimeItem(
                city = city,
                country = country,
                timezoneId = zoneStr,
                gmtOffset = gmtFormatted,
                timeFormatted = zdt.format(timeFormatter),
                dateFormatted = zdt.format(dateFormatter),
                isDaytime = isDay
            )
        }
    }
}
