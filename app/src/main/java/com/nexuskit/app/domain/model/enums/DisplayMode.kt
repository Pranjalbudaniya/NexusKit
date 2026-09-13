package com.nexuskit.app.domain.model.enums

/**
 * Controls how tool items are rendered inside a category.
 *
 * CARD      → icon + name, medium card tile
 * PILL      → name only, compact pill/chip shape
 * LIST      → icon + name + description, full-width row
 * ICON_GRID → icon only, small square tiles
 */
enum class DisplayMode(val displayName: String) {
    CARD("Cards"),
    PILL("Pills"),
    LIST("List"),
    ICON_GRID("Icons");

    companion object {
        fun fromName(name: String): DisplayMode =
            entries.firstOrNull { it.name == name } ?: CARD
    }
}
