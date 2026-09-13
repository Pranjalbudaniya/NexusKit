package com.nexuskit.app.feature.tools.unit_converter

import java.text.DecimalFormat

enum class UnitCategory(val displayName: String, val iconName: String) {
    LENGTH("Length", "Straighten"),
    MASS("Mass & Weight", "FitnessCenter"),
    TEMPERATURE("Temperature", "Thermostat"),
    AREA("Area", "SquareFoot"),
    VOLUME("Volume", "LocalDrink"),
    SPEED("Speed", "Speed"),
    TIME("Time", "Schedule"),
    DIGITAL("Digital Storage", "Storage")
}

data class ConversionUnit(
    val id: String,
    val name: String,
    val symbol: String,
    val category: UnitCategory,
    val toBaseFactor: Double = 1.0,  // Multiply by this to get base unit (or custom logic for Temp)
    val isTemperature: Boolean = false
)

object UnitConverterEngine {

    val allUnits: Map<UnitCategory, List<ConversionUnit>> = mapOf(
        UnitCategory.LENGTH to listOf(
            ConversionUnit("m", "Meter", "m", UnitCategory.LENGTH, 1.0),
            ConversionUnit("km", "Kilometer", "km", UnitCategory.LENGTH, 1000.0),
            ConversionUnit("cm", "Centimeter", "cm", UnitCategory.LENGTH, 0.01),
            ConversionUnit("mm", "Millimeter", "mm", UnitCategory.LENGTH, 0.001),
            ConversionUnit("mi", "Mile", "mi", UnitCategory.LENGTH, 1609.344),
            ConversionUnit("yd", "Yard", "yd", UnitCategory.LENGTH, 0.9144),
            ConversionUnit("ft", "Foot", "ft", UnitCategory.LENGTH, 0.3048),
            ConversionUnit("in", "Inch", "in", UnitCategory.LENGTH, 0.0254),
            ConversionUnit("nmi", "Nautical Mile", "NM", UnitCategory.LENGTH, 1852.0)
        ),
        UnitCategory.MASS to listOf(
            ConversionUnit("kg", "Kilogram", "kg", UnitCategory.MASS, 1.0),
            ConversionUnit("g", "Gram", "g", UnitCategory.MASS, 0.001),
            ConversionUnit("mg", "Milligram", "mg", UnitCategory.MASS, 0.000001),
            ConversionUnit("t", "Metric Ton", "t", UnitCategory.MASS, 1000.0),
            ConversionUnit("lb", "Pound", "lb", UnitCategory.MASS, 0.45359237),
            ConversionUnit("oz", "Ounce", "oz", UnitCategory.MASS, 0.028349523125),
            ConversionUnit("st", "Stone", "st", UnitCategory.MASS, 6.35029318)
        ),
        UnitCategory.TEMPERATURE to listOf(
            ConversionUnit("c", "Celsius", "°C", UnitCategory.TEMPERATURE, isTemperature = true),
            ConversionUnit("f", "Fahrenheit", "°F", UnitCategory.TEMPERATURE, isTemperature = true),
            ConversionUnit("k", "Kelvin", "K", UnitCategory.TEMPERATURE, isTemperature = true)
        ),
        UnitCategory.AREA to listOf(
            ConversionUnit("m2", "Square Meter", "m²", UnitCategory.AREA, 1.0),
            ConversionUnit("km2", "Square Kilometer", "km²", UnitCategory.AREA, 1_000_000.0),
            ConversionUnit("cm2", "Square Centimeter", "cm²", UnitCategory.AREA, 0.0001),
            ConversionUnit("ha", "Hectare", "ha", UnitCategory.AREA, 10_000.0),
            ConversionUnit("ac", "Acre", "ac", UnitCategory.AREA, 4046.8564224),
            ConversionUnit("sqft", "Square Foot", "sq ft", UnitCategory.AREA, 0.09290304),
            ConversionUnit("sqmi", "Square Mile", "sq mi", UnitCategory.AREA, 2589988.110336)
        ),
        UnitCategory.VOLUME to listOf(
            ConversionUnit("l", "Liter", "L", UnitCategory.VOLUME, 1.0),
            ConversionUnit("ml", "Milliliter", "mL", UnitCategory.VOLUME, 0.001),
            ConversionUnit("m3", "Cubic Meter", "m³", UnitCategory.VOLUME, 1000.0),
            ConversionUnit("gal", "US Gallon", "gal", UnitCategory.VOLUME, 3.785411784),
            ConversionUnit("qt", "US Quart", "qt", UnitCategory.VOLUME, 0.946352946),
            ConversionUnit("pt", "US Pint", "pt", UnitCategory.VOLUME, 0.473176473),
            ConversionUnit("cup", "US Cup", "cup", UnitCategory.VOLUME, 0.2365882365),
            ConversionUnit("fl_oz", "US Fluid Ounce", "fl oz", UnitCategory.VOLUME, 0.0295735295625)
        ),
        UnitCategory.SPEED to listOf(
            ConversionUnit("mps", "Meters per second", "m/s", UnitCategory.SPEED, 1.0),
            ConversionUnit("kmh", "Kilometers per hour", "km/h", UnitCategory.SPEED, 1.0 / 3.6),
            ConversionUnit("mph", "Miles per hour", "mph", UnitCategory.SPEED, 0.44704),
            ConversionUnit("knot", "Knot", "kn", UnitCategory.SPEED, 0.514444)
        ),
        UnitCategory.TIME to listOf(
            ConversionUnit("s", "Second", "s", UnitCategory.TIME, 1.0),
            ConversionUnit("ms", "Millisecond", "ms", UnitCategory.TIME, 0.001),
            ConversionUnit("min", "Minute", "min", UnitCategory.TIME, 60.0),
            ConversionUnit("h", "Hour", "hr", UnitCategory.TIME, 3600.0),
            ConversionUnit("d", "Day", "day", UnitCategory.TIME, 86400.0),
            ConversionUnit("wk", "Week", "wk", UnitCategory.TIME, 604800.0),
            ConversionUnit("yr", "Year (365d)", "yr", UnitCategory.TIME, 31536000.0)
        ),
        UnitCategory.DIGITAL to listOf(
            ConversionUnit("b", "Byte", "B", UnitCategory.DIGITAL, 1.0),
            ConversionUnit("kb", "Kilobyte (KB)", "KB", UnitCategory.DIGITAL, 1024.0),
            ConversionUnit("mb", "Megabyte (MB)", "MB", UnitCategory.DIGITAL, 1024.0 * 1024),
            ConversionUnit("gb", "Gigabyte (GB)", "GB", UnitCategory.DIGITAL, 1024.0 * 1024 * 1024),
            ConversionUnit("tb", "Terabyte (TB)", "TB", UnitCategory.DIGITAL, 1024.0 * 1024 * 1024 * 1024),
            ConversionUnit("bit", "Bit", "bit", UnitCategory.DIGITAL, 0.125)
        )
    )

    fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
        if (from.id == to.id) return value

        if (from.isTemperature) {
            // Convert to Celsius first
            val celsius = when (from.id) {
                "c" -> value
                "f" -> (value - 32.0) * (5.0 / 9.0)
                "k" -> value - 273.15
                else -> value
            }
            // Convert from Celsius to Target
            return when (to.id) {
                "c" -> celsius
                "f" -> (celsius * (9.0 / 5.0)) + 32.0
                "k" -> celsius + 273.15
                else -> celsius
            }
        }

        val baseValue = value * from.toBaseFactor
        return baseValue / to.toBaseFactor
    }

    private val sciDf = DecimalFormat("0.####E0", java.text.DecimalFormatSymbols(java.util.Locale.US))
    private val stdDf = DecimalFormat("#,##0.######", java.text.DecimalFormatSymbols(java.util.Locale.US))

    fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite() || value == 0.0) return "0"

        val abs = kotlin.math.abs(value)
        return synchronized(this) {
            if (abs >= 1e9 || (abs < 1e-4 && abs > 0.0)) {
                sciDf.format(value)
            } else {
                stdDf.format(value)
            }
        }
    }
}
