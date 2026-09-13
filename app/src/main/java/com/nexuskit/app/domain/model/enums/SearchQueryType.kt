package com.nexuskit.app.domain.model.enums

/**
 * The interpreted type of a search query.
 * Determined by ParseSearchQueryUseCase (Spec 06).
 *
 * MATH            → query is a math expression  (e.g. "3+4", "sqrt(25)")
 * UNIT_CONVERSION → query is a unit value       (e.g. "5kg", "100f", "10mi")
 * TOOL_SEARCH     → query matches tool names / descriptions / keywords
 * MIXED           → matches both tools and produces a calculation/conversion
 * EMPTY           → query is blank, nothing to show
 */
enum class SearchQueryType {
    MATH,
    UNIT_CONVERSION,
    TOOL_SEARCH,
    MIXED,
    EMPTY
}
