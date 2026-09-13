package com.nexuskit.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexuskit.app.domain.model.UserPreferences
import com.nexuskit.app.domain.model.enums.ThemeMode

/**
 * Root Compose theme for NexusKit.
 * Reads live UserPreferences and applies:
 *   • ColorScheme  — dynamic (API 31+) OR seed-generated
 *   • ThemeMode    — light / dark / amoled / system
 *   • Typography   — font family from UserPreferences
 *   • Spacing      — spacing mode from UserPreferences
 *
 * Called directly from MainActivity.
 * All UI inside this composable can access:
 *   MaterialTheme.colorScheme.*   — colors
 *   MaterialTheme.typography.*    — text styles
 *   LocalSpacing.current.*        — spacing tokens
 */
@Composable
fun NexusKitTheme(
    viewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val prefs by viewModel.preferences.collectAsState(initial = UserPreferences())
    NexusKitThemeContent(prefs = prefs, content = content)
}

/**
 * Separated for testability — takes UserPreferences directly.
 * Use this in @Preview functions.
 */
@Composable
fun NexusKitThemeContent(
    prefs: UserPreferences = UserPreferences(),
    content: @Composable () -> Unit
) {
    val context        = LocalContext.current
    val systemIsDark   = isSystemInDarkTheme()

    val isDark = when (prefs.themeMode) {
        ThemeMode.LIGHT  -> false
        ThemeMode.DARK   -> true
        ThemeMode.AMOLED -> true
        ThemeMode.SYSTEM -> systemIsDark
    }

    // ── Build color scheme (Memoized) ─────────────────────────────
    val colorScheme = androidx.compose.runtime.remember(
        prefs.themeMode,
        isDark,
        prefs.dynamicColorEnabled,
        prefs.accentColorArgb
    ) {
        val baseScheme = when {
            // Dynamic (Material You) — API 31+ only
            prefs.dynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (isDark) dynamicDarkColorScheme(context)
                else        dynamicLightColorScheme(context)
            }
            // Custom or preset seed color
            prefs.accentColorArgb != null -> {
                ColorSchemeGenerator.fromSeed(
                    seedArgb = prefs.accentColorArgb,
                    isDark   = isDark
                )
            }
            // Fallback — default sky blue seed
            else -> {
                ColorSchemeGenerator.fromSeed(
                    seedArgb = PresetThemes.default.argb,
                    isDark   = isDark
                )
            }
        }

        if (prefs.themeMode == ThemeMode.AMOLED) {
            ColorSchemeGenerator.applyAmoledOverride(baseScheme)
        } else {
            baseScheme
        }
    }

    // ── Typography (Memoized) ─────────────────────────────────────
    val typography = androidx.compose.runtime.remember(prefs.fontFamily) {
        buildTypography(prefs.fontFamily)
    }

    // ── Spacing (Memoized) ────────────────────────────────────────
    val spacing = androidx.compose.runtime.remember(prefs.spacingMode) {
        NexusKitSpacing.forMode(prefs.spacingMode)
    }

    CompositionLocalProvider(
        LocalSpacing provides spacing
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = typography,
            content     = content
        )
    }
}
