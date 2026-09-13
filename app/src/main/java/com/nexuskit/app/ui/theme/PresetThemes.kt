package com.nexuskit.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * The 8 built-in preset themes shown in the colour picker bottom sheet.
 *
 * [seed]  is the Color that feeds into ColorSchemeGenerator.
 * [argb]  is the Long ARGB stored in DataStore when this preset is selected.
 * [label] is the display name shown in the picker.
 *
 * When user picks a preset, store presetTheme.argb via updateAccentColor(argb).
 * The Theme engine reads accentColorArgb from UserPreferences and calls
 * ColorSchemeGenerator.fromSeed(accentColorArgb).
 */
data class PresetTheme(
    val label: String,
    val seed: Color,
    val argb: Long
)

object PresetThemes {

    val all: List<PresetTheme> = listOf(
        PresetTheme("Sky",      SeedSkyBlue,  0xFF0288D1L),
        PresetTheme("Lavender", SeedLavender, 0xFF7C4DFFL),
        PresetTheme("Forest",   SeedForest,   0xFF2E7D32L),
        PresetTheme("Coral",    SeedCoral,    0xFFE64A19L),
        PresetTheme("Rose",     SeedRose,     0xFFC2185BL),
        PresetTheme("Teal",     SeedTeal,     0xFF00796BL),
        PresetTheme("Amber",    SeedAmber,    0xFFF57F17L),
        PresetTheme("Indigo",   SeedIndigo,   0xFF303F9FL)
    )

    val default: PresetTheme = all.first()   // Sky Blue

    fun isPreset(argb: Long): Boolean = all.any { it.argb == argb }
}
