package com.nexuskit.app.domain.model.enums

enum class SpacingMode(val displayName: String, val multiplier: Float) {
    COMPACT("Compact",       0.75f),
    COMFORTABLE("Comfortable", 1.0f),
    SPACIOUS("Spacious",    1.35f);

    companion object {
        fun fromName(name: String): SpacingMode =
            entries.firstOrNull { it.name == name } ?: COMFORTABLE
    }
}
