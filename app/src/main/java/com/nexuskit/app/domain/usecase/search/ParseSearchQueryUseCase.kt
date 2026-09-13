package com.nexuskit.app.domain.usecase.search

import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.core.base.SuspendUseCase
import com.nexuskit.app.domain.model.MathResult
import com.nexuskit.app.domain.model.SearchResult
import com.nexuskit.app.domain.model.UnitConversionResult
import com.nexuskit.app.domain.model.enums.SearchQueryType
import com.nexuskit.app.domain.model.enums.UnitCategory
import com.nexuskit.app.domain.repository.IToolRepository
import net.objecthunter.exp4j.ExpressionBuilder
import javax.inject.Inject
import kotlin.math.abs

data class ParseSearchParams(val query: String)

/**
 * Smart search query parser. Determines query type and returns a SearchResult
 * that the search UI renders inline above the keyboard.
 *
 * Priority order:
 *   1. Blank / too short   → EMPTY
 *   2. Unit conversion     → UNIT_CONVERSION  (e.g. "5kg", "100f", "72°f")
 *   3. Math expression     → MATH             (e.g. "3+4", "sqrt(25)", "15% of 200")
 *   4. Tool name match     → TOOL_SEARCH
 *   5. Both math + tools   → MIXED
 *
 * Edge cases handled:
 *   • Division by zero          → MathResult(isValid=false, result="Cannot divide by zero")
 *   • Overflow / NaN / Infinity → MathResult(isValid=false, result="Result too large")
 *   • Unknown unit              → no unit conversions, falls through to tool search
 *   • Temperature conversion    → handled via formula (not multiplier)
 *   • "0" or bare number        → treated as potential math AND tool search
 *   • Query > 200 chars         → capped before parsing
 */
class ParseSearchQueryUseCase @Inject constructor(
    private val toolRepository: IToolRepository
) : SuspendUseCase<ParseSearchParams, SearchResult>() {

    override suspend fun execute(params: ParseSearchParams): Resource<SearchResult> {
        val raw = params.query.trim().take(200)
        if (raw.isBlank()) return Resource.Success(SearchResult(queryType = SearchQueryType.EMPTY))

        return try {
            val mathResult    = tryParseMath(raw)
            val unitResults   = tryParseUnit(raw)
            val matchedTools  = toolRepository.searchTools(raw)

            val queryType = when {
                unitResults.isNotEmpty() && matchedTools.isNotEmpty() -> SearchQueryType.MIXED
                unitResults.isNotEmpty()                              -> SearchQueryType.UNIT_CONVERSION
                mathResult != null && matchedTools.isNotEmpty()       -> SearchQueryType.MIXED
                mathResult != null                                    -> SearchQueryType.MATH
                matchedTools.isNotEmpty()                             -> SearchQueryType.TOOL_SEARCH
                else                                                  -> SearchQueryType.EMPTY
            }

            Resource.Success(
                SearchResult(
                    query           = raw,
                    queryType       = queryType,
                    mathResult      = mathResult,
                    unitConversions = unitResults,
                    matchedTools    = matchedTools
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Search parse failed", e)
        }
    }

    companion object {
        private val MATH_PATTERN = Regex("[0-9+\\-*/%^().\\s√a-zA-Z]+")
        private val UNIT_PATTERN = Regex("^([0-9]*\\.?[0-9]+)\\s*([a-zA-Z°/]+)\$")
    }

    // ── Math ─────────────────────────────────────────────────────────────────

    private fun tryParseMath(query: String): MathResult? {
        // Must contain at least one operator to be treated as math
        val cleaned = query.replace("×", "*").replace("÷", "/").trim()
        if (!cleaned.any { it in "+-*/%^" }) return null
        if (!cleaned.matches(MATH_PATTERN)) return null

        return try {
            val expr = ExpressionBuilder(cleaned).build()
            val value = expr.evaluate()

            when {
                value.isNaN()                     -> MathResult(cleaned, "Not a number",        false)
                value.isInfinite()                -> MathResult(cleaned, "Cannot divide by zero", false)
                abs(value) > 1e15                 -> MathResult(cleaned, "Result too large",    false)
                value == value.toLong().toDouble() -> MathResult(cleaned, value.toLong().toString(), true)
                else                              -> MathResult(cleaned, "%.6g".format(value).trimEnd('0').trimEnd('.'), true)
            }
        } catch (e: Exception) {
            null // Not a valid expression — fall through to other parsers
        }
    }

    // ── Unit Conversion ───────────────────────────────────────────────────────

    private fun tryParseUnit(query: String): List<UnitConversionResult> {
        val match = UNIT_PATTERN.matchEntire(query.trim()) ?: return emptyList()
        val value = match.groupValues[1].toDoubleOrNull() ?: return emptyList()
        val unit  = match.groupValues[2].lowercase()

        // Temperature is handled separately (formula-based, not multiplier-based)
        if (unit in temperatureUnits) return convertTemperature(value, unit)

        val (category, toBase) = unitTable[unit] ?: return emptyList()
        val valueInBase = value * toBase

        return unitTable.entries
            .filter { (k, v) -> v.first == category && k != unit }
            .map { (targetUnit, targetData) ->
                val converted = valueInBase / targetData.second
                UnitConversionResult(
                    inputValue           = value,
                    inputUnit            = unit,
                    inputUnitDisplayName = unitDisplayNames[unit] ?: unit.uppercase(),
                    outputValue          = formatConvertedValue(converted),
                    outputUnit           = targetUnit,
                    outputUnitDisplayName= unitDisplayNames[targetUnit] ?: targetUnit.uppercase(),
                    category             = category
                )
            }
            .sortedBy { it.outputUnit }
    }

    private fun convertTemperature(value: Double, fromUnit: String): List<UnitConversionResult> {
        val celsius = when (fromUnit) {
            "c", "°c" -> value
            "f", "°f" -> (value - 32) * 5.0 / 9.0
            "k"       -> value - 273.15
            else      -> return emptyList()
        }
        return listOf(
            tempResult(value, fromUnit, celsius,            "c",  "°C",  "Celsius"),
            tempResult(value, fromUnit, celsius * 9/5 + 32,"f",  "°F",  "Fahrenheit"),
            tempResult(value, fromUnit, celsius + 273.15,  "k",  "K",   "Kelvin")
        ).filter { it.outputUnit != fromUnit.trimStart('°') }
    }

    private fun tempResult(
        input: Double, fromUnit: String, result: Double,
        toUnit: String, toSymbol: String, toName: String
    ) = UnitConversionResult(
        inputValue            = input,
        inputUnit             = fromUnit,
        inputUnitDisplayName  = unitDisplayNames[fromUnit] ?: fromUnit.uppercase(),
        outputValue           = formatConvertedValue(result),
        outputUnit            = toUnit,
        outputUnitDisplayName = toName,
        category              = UnitCategory.TEMPERATURE
    )

    private fun formatConvertedValue(value: Double): String = when {
        value == 0.0                          -> "0"
        value == value.toLong().toDouble()    -> value.toLong().toString()
        abs(value) >= 1000 || abs(value) < 0.01 -> "%.4e".format(value)
        else                                  -> "%.4f".format(value).trimEnd('0').trimEnd('.')
    }

    // ── Unit Tables ───────────────────────────────────────────────────────────

    private val temperatureUnits = setOf("c", "°c", "f", "°f", "k")

    /** unit → Pair(category, conversionFactorToBase) */
    private val unitTable: Map<String, Pair<UnitCategory, Double>> = mapOf(
        // Weight — base: kg
        "kg"    to (UnitCategory.WEIGHT to 1.0),
        "g"     to (UnitCategory.WEIGHT to 0.001),
        "mg"    to (UnitCategory.WEIGHT to 0.000001),
        "lb"    to (UnitCategory.WEIGHT to 0.453592),
        "lbs"   to (UnitCategory.WEIGHT to 0.453592),
        "oz"    to (UnitCategory.WEIGHT to 0.0283495),
        "t"     to (UnitCategory.WEIGHT to 1000.0),
        "st"    to (UnitCategory.WEIGHT to 6.35029),
        // Length — base: m
        "m"     to (UnitCategory.LENGTH to 1.0),
        "km"    to (UnitCategory.LENGTH to 1000.0),
        "cm"    to (UnitCategory.LENGTH to 0.01),
        "mm"    to (UnitCategory.LENGTH to 0.001),
        "mi"    to (UnitCategory.LENGTH to 1609.34),
        "ft"    to (UnitCategory.LENGTH to 0.3048),
        "in"    to (UnitCategory.LENGTH to 0.0254),
        "yd"    to (UnitCategory.LENGTH to 0.9144),
        "nm"    to (UnitCategory.LENGTH to 1852.0),
        // Data — base: bytes
        "b"     to (UnitCategory.DATA to 1.0),
        "kb"    to (UnitCategory.DATA to 1024.0),
        "mb"    to (UnitCategory.DATA to 1_048_576.0),
        "gb"    to (UnitCategory.DATA to 1_073_741_824.0),
        "tb"    to (UnitCategory.DATA to 1_099_511_627_776.0),
        "pb"    to (UnitCategory.DATA to 1_125_899_906_842_624.0),
        // Speed — base: m/s
        "mph"   to (UnitCategory.SPEED to 0.44704),
        "kph"   to (UnitCategory.SPEED to 0.277778),
        "kmh"   to (UnitCategory.SPEED to 0.277778),
        "knot"  to (UnitCategory.SPEED to 0.514444),
        // Time — base: seconds
        "s"     to (UnitCategory.TIME to 1.0),
        "min"   to (UnitCategory.TIME to 60.0),
        "hr"    to (UnitCategory.TIME to 3600.0),
        "h"     to (UnitCategory.TIME to 3600.0),
        "day"   to (UnitCategory.TIME to 86400.0),
        "wk"    to (UnitCategory.TIME to 604800.0),
        "mo"    to (UnitCategory.TIME to 2_629_800.0),
        "yr"    to (UnitCategory.TIME to 31_557_600.0)
    )

    private val unitDisplayNames: Map<String, String> = mapOf(
        "kg" to "Kilograms", "g" to "Grams", "mg" to "Milligrams",
        "lb" to "Pounds", "lbs" to "Pounds", "oz" to "Ounces",
        "t" to "Tonnes", "st" to "Stone",
        "m" to "Metres", "km" to "Kilometres", "cm" to "Centimetres",
        "mm" to "Millimetres", "mi" to "Miles", "ft" to "Feet",
        "in" to "Inches", "yd" to "Yards", "nm" to "Nautical Miles",
        "c" to "Celsius", "°c" to "Celsius",
        "f" to "Fahrenheit", "°f" to "Fahrenheit",
        "k" to "Kelvin",
        "b" to "Bytes", "kb" to "Kilobytes", "mb" to "Megabytes",
        "gb" to "Gigabytes", "tb" to "Terabytes", "pb" to "Petabytes",
        "mph" to "Miles/hour", "kph" to "Km/hour", "kmh" to "Km/hour",
        "knot" to "Knots",
        "s" to "Seconds", "min" to "Minutes", "hr" to "Hours",
        "h" to "Hours", "day" to "Days", "wk" to "Weeks",
        "mo" to "Months", "yr" to "Years"
    )
}
