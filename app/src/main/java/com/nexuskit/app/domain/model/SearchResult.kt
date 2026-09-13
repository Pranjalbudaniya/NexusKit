package com.nexuskit.app.domain.model

import com.nexuskit.app.domain.model.enums.SearchQueryType
import com.nexuskit.app.domain.model.enums.UnitCategory

/**
 * The full result of a smart search query.
 * Produced by ParseSearchQueryUseCase (Spec 06).
 *
 * A single query can produce multiple result types simultaneously — for example,
 * "5kg" produces unit conversions AND might match the "Unit Converter" tool.
 * That combination is queryType = MIXED.
 */
data class SearchResult(
    val query: String = "",
    val queryType: SearchQueryType = SearchQueryType.EMPTY,
    val mathResult: MathResult? = null,
    val unitConversions: List<UnitConversionResult> = emptyList(),
    val matchedTools: List<ToolInfo> = emptyList()
) {
    val isEmpty: Boolean
        get() = mathResult == null &&
                unitConversions.isEmpty() &&
                matchedTools.isEmpty()
}

/**
 * Result of evaluating a math expression in the search bar.
 * [expression] is the original query as typed.
 * [result]     is the formatted result string (e.g. "7", "3.14", "Error").
 * [isValid]    false if the expression could not be evaluated.
 */
data class MathResult(
    val expression: String,
    val result: String,
    val isValid: Boolean = true
)

/**
 * A single unit-to-unit conversion result.
 * [inputValue]             The numeric value entered by the user.
 * [inputUnit]              The unit abbreviation the user typed (e.g. "kg").
 * [inputUnitDisplayName]   Human-readable input unit name (e.g. "Kilograms").
 * [outputValue]            Formatted converted value string (e.g. "2.205").
 * [outputUnit]             The target unit abbreviation (e.g. "lb").
 * [outputUnitDisplayName]  Human-readable target unit name (e.g. "Pounds").
 * [category]               Which unit category this conversion belongs to.
 */
data class UnitConversionResult(
    val inputValue: Double,
    val inputUnit: String,
    val inputUnitDisplayName: String,
    val outputValue: String,
    val outputUnit: String,
    val outputUnitDisplayName: String,
    val category: UnitCategory
)
