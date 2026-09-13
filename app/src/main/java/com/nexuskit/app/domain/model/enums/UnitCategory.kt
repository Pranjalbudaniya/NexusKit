package com.nexuskit.app.domain.model.enums

/**
 * Categories of unit conversion supported by the smart search and unit converter tool.
 * Each category groups related units together for display in search results.
 */
enum class UnitCategory(val displayName: String) {
    WEIGHT("Weight"),
    LENGTH("Length"),
    TEMPERATURE("Temperature"),
    AREA("Area"),
    VOLUME("Volume"),
    SPEED("Speed"),
    DATA("Data Size"),
    TIME("Time"),
    PRESSURE("Pressure"),
    ENERGY("Energy");
}
