package com.nexuskit.app.domain.model.enums

/**
 * Controls the drop shadow / elevation of cards on the home screen.
 * [dp] is the actual elevation value used in Compose (in dp units).
 */
enum class ShadowElevation(val displayName: String, val dp: Float) {
    NONE("None",    0f),
    LOW("Low",      2f),
    MEDIUM("Medium",6f),
    HIGH("High",    12f);

    companion object {
        fun fromName(name: String): ShadowElevation =
            entries.firstOrNull { it.name == name } ?: MEDIUM
    }
}
