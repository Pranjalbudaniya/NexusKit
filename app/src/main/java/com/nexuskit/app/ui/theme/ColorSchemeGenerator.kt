package com.nexuskit.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import material.DynamicColor
import material.Hct
import material.MaterialDynamicColors
import material.SchemeTonalSpot

/**
 * Generates a full Material 3 ColorScheme from a seed color Long.
 * Uses Google's material-color-utilities library (same engine as Material You).
 *
 * Called by NexusKitTheme when dynamic color is OFF.
 * When dynamic color is ON, Android's own dynamicLightColorScheme /
 * dynamicDarkColorScheme are used instead (API 31+ only).
 */
object ColorSchemeGenerator {

    private val dynamicColors = MaterialDynamicColors()

    /**
     * Generates a light or dark ColorScheme from [seedArgb].
     * [contrast] range: -1.0 (low) to 1.0 (high). Default 0.0 = standard M3.
     */
    fun fromSeed(
        seedArgb: Long,
        isDark: Boolean,
        contrast: Double = 0.0
    ): ColorScheme {
        val hct    = Hct.fromInt(seedArgb.toInt())
        val scheme = SchemeTonalSpot(hct, isDark, contrast)

        fun color(token: () -> DynamicColor) =
            Color(token().getArgb(scheme))

        return if (isDark) {
            darkColorScheme(
                primary                = color { dynamicColors.primary() },
                onPrimary              = color { dynamicColors.onPrimary() },
                primaryContainer       = color { dynamicColors.primaryContainer() },
                onPrimaryContainer     = color { dynamicColors.onPrimaryContainer() },
                secondary              = color { dynamicColors.secondary() },
                onSecondary            = color { dynamicColors.onSecondary() },
                secondaryContainer     = color { dynamicColors.secondaryContainer() },
                onSecondaryContainer   = color { dynamicColors.onSecondaryContainer() },
                tertiary               = color { dynamicColors.tertiary() },
                onTertiary             = color { dynamicColors.onTertiary() },
                tertiaryContainer      = color { dynamicColors.tertiaryContainer() },
                onTertiaryContainer    = color { dynamicColors.onTertiaryContainer() },
                error                  = color { dynamicColors.error() },
                onError                = color { dynamicColors.onError() },
                errorContainer         = color { dynamicColors.errorContainer() },
                onErrorContainer       = color { dynamicColors.onErrorContainer() },
                background             = color { dynamicColors.background() },
                onBackground           = color { dynamicColors.onBackground() },
                surface                = color { dynamicColors.surface() },
                onSurface              = color { dynamicColors.onSurface() },
                surfaceVariant         = color { dynamicColors.surfaceVariant() },
                onSurfaceVariant       = color { dynamicColors.onSurfaceVariant() },
                outline                = color { dynamicColors.outline() },
                outlineVariant         = color { dynamicColors.outlineVariant() },
                inverseSurface         = color { dynamicColors.inverseSurface() },
                inverseOnSurface       = color { dynamicColors.inverseOnSurface() },
                inversePrimary         = color { dynamicColors.inversePrimary() },
                surfaceTint            = color { dynamicColors.primary() }
            )
        } else {
            lightColorScheme(
                primary                = color { dynamicColors.primary() },
                onPrimary              = color { dynamicColors.onPrimary() },
                primaryContainer       = color { dynamicColors.primaryContainer() },
                onPrimaryContainer     = color { dynamicColors.onPrimaryContainer() },
                secondary              = color { dynamicColors.secondary() },
                onSecondary            = color { dynamicColors.onSecondary() },
                secondaryContainer     = color { dynamicColors.secondaryContainer() },
                onSecondaryContainer   = color { dynamicColors.onSecondaryContainer() },
                tertiary               = color { dynamicColors.tertiary() },
                onTertiary             = color { dynamicColors.onTertiary() },
                tertiaryContainer      = color { dynamicColors.tertiaryContainer() },
                onTertiaryContainer    = color { dynamicColors.onTertiaryContainer() },
                error                  = color { dynamicColors.error() },
                onError                = color { dynamicColors.onError() },
                errorContainer         = color { dynamicColors.errorContainer() },
                onErrorContainer       = color { dynamicColors.onErrorContainer() },
                background             = color { dynamicColors.background() },
                onBackground           = color { dynamicColors.onBackground() },
                surface                = color { dynamicColors.surface() },
                onSurface              = color { dynamicColors.onSurface() },
                surfaceVariant         = color { dynamicColors.surfaceVariant() },
                onSurfaceVariant       = color { dynamicColors.onSurfaceVariant() },
                outline                = color { dynamicColors.outline() },
                outlineVariant         = color { dynamicColors.outlineVariant() },
                inverseSurface         = color { dynamicColors.inverseSurface() },
                inverseOnSurface       = color { dynamicColors.inverseOnSurface() },
                inversePrimary         = color { dynamicColors.inversePrimary() },
                surfaceTint            = color { dynamicColors.primary() }
            )
        }
    }

    /**
     * Applies AMOLED overrides to a dark ColorScheme.
     * Replaces background/surface with near-black values.
     * Only called when themeMode == AMOLED.
     */
    fun applyAmoledOverride(scheme: ColorScheme): ColorScheme = scheme.copy(
        background    = AmoledBlack,
        surface       = AmoledSurface,
        surfaceVariant= AmoledSurfaceVariant,
    )
}
