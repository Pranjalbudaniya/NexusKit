package com.nexuskit.app.domain.model.enums

enum class StatusBarStyle(val displayName: String) {
    TRANSPARENT("Transparent"),
    COLORED("App Accent"),
    SYSTEM_DEFAULT("System Default");

    companion object {
        fun fromName(name: String): StatusBarStyle =
            entries.firstOrNull { it.name == name } ?: TRANSPARENT
    }
}
