package com.nexuskit.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * NexusKit color constants.
 * This is the ONLY file in the project that may define Color(0xFFxxxxxx) values.
 * Everywhere else: use MaterialTheme.colorScheme.* tokens only.
 */

// ── Preset Theme Seed Colors ──────────────────────────────────────
// These seed colors feed into ColorSchemeGenerator to produce full M3 palettes.
// "Seed" = the hue anchor. M3 generates 40+ tonal variants from each.

val SeedSkyBlue      = Color(0xFF0288D1)   // Default — light blue
val SeedLavender     = Color(0xFF7C4DFF)   // Purple
val SeedForest       = Color(0xFF2E7D32)   // Green
val SeedCoral        = Color(0xFFE64A19)   // Orange-red
val SeedRose         = Color(0xFFC2185B)   // Pink
val SeedTeal         = Color(0xFF00796B)   // Teal
val SeedAmber        = Color(0xFFF57F17)   // Amber/gold
val SeedIndigo       = Color(0xFF303F9F)   // Deep indigo

// ── AMOLED Override Colors ────────────────────────────────────────
// Used to replace background/surface in dark theme when AMOLED mode is on.
// Pure black for pixels-off efficiency on OLED screens.

val AmoledBlack         = Color(0xFF000000)
val AmoledSurface       = Color(0xFF080808)
val AmoledSurfaceVariant= Color(0xFF111111)
val AmoledContainer     = Color(0xFF1A1A1A)
