package com.nexuskit.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.nexuskit.app.R
import java.util.concurrent.ConcurrentHashMap
import com.nexuskit.app.domain.model.enums.FontFamily as AppFontFamily

// ── Google Fonts Provider ─────────────────────────────────────────────────────
val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

fun googleFamily(name: String): FontFamily {
    val font = GoogleFont(name)
    return FontFamily(
        Font(googleFont = font, fontProvider = googleFontProvider, weight = FontWeight.Normal),
        Font(googleFont = font, fontProvider = googleFontProvider, weight = FontWeight.Medium),
        Font(googleFont = font, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
        Font(googleFont = font, fontProvider = googleFontProvider, weight = FontWeight.Bold)
    )
}

private val InterFamily       by lazy { googleFamily("Inter") }
private val PoppinsFamily     by lazy { googleFamily("Poppins") }
private val NunitoFamily      by lazy { googleFamily("Nunito") }
private val MontserratFamily  by lazy { googleFamily("Montserrat") }
private val LatoFamily        by lazy { googleFamily("Lato") }
private val RalewayFamily     by lazy { googleFamily("Raleway") }
private val SourceSansFamily  by lazy { googleFamily("Source Sans 3") }
private val DmSansFamily      by lazy { googleFamily("DM Sans") }
private val LexendFamily      by lazy { googleFamily("Lexend") }

fun resolveFontFamily(fontFamily: AppFontFamily): FontFamily? {
    return when (fontFamily) {
        AppFontFamily.SYSTEM_DEFAULT -> null
        AppFontFamily.ROBOTO         -> FontFamily.SansSerif
        AppFontFamily.INTER          -> InterFamily
        AppFontFamily.POPPINS        -> PoppinsFamily
        AppFontFamily.NUNITO         -> NunitoFamily
        AppFontFamily.MONTSERRAT     -> MontserratFamily
        AppFontFamily.LATO           -> LatoFamily
        AppFontFamily.RALEWAY        -> RalewayFamily
        AppFontFamily.SOURCE_SANS    -> SourceSansFamily
        AppFontFamily.DM_SANS        -> DmSansFamily
        AppFontFamily.LEXEND         -> LexendFamily
    }
}

private val typographyCache = ConcurrentHashMap<AppFontFamily, Typography>()

/**
 * Builds a full Material 3 Typography from the user's chosen [AppFontFamily].
 * Called by NexusKitThemeContent every time FontFamily preference changes.
 * Results are cached in memory for instant reuse.
 */
fun buildTypography(fontFamily: AppFontFamily): Typography {
    return typographyCache.getOrPut(fontFamily) {
        val family: FontFamily? = resolveFontFamily(fontFamily)

        if (family == null) {
            Typography()
        } else {
            val base = Typography()
            fun TextStyle.withFamily() = copy(fontFamily = family)

            Typography(
                displayLarge       = base.displayLarge.withFamily(),
                displayMedium      = base.displayMedium.withFamily(),
                displaySmall       = base.displaySmall.withFamily(),
                headlineLarge      = base.headlineLarge.withFamily(),
                headlineMedium     = base.headlineMedium.withFamily(),
                headlineSmall      = base.headlineSmall.withFamily(),
                titleLarge         = base.titleLarge.withFamily(),
                titleMedium        = base.titleMedium.withFamily(),
                titleSmall         = base.titleSmall.withFamily(),
                bodyLarge          = base.bodyLarge.withFamily(),
                bodyMedium         = base.bodyMedium.withFamily(),
                bodySmall          = base.bodySmall.withFamily(),
                labelLarge         = base.labelLarge.withFamily(),
                labelMedium        = base.labelMedium.withFamily(),
                labelSmall         = base.labelSmall.withFamily()
            )
        }
    }
}
