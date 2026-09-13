package com.nexuskit.app.domain.model.enums

enum class ThemeMode(val displayName: String) {
    SYSTEM("Follow System"),
    LIGHT("Light"),
    DARK("Dark"),
    AMOLED("AMOLED Black");

    companion object {
        fun fromName(name: String): ThemeMode =
            entries.firstOrNull { it.name == name } ?: SYSTEM
    }
}
