package com.nexuskit.app.feature.tools.color_palette

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class ColorSwatch(
    val name: String,
    val color: Color,
    val hex: String,
    val rgb: String,
    val hsl: String
)

data class ContrastReport(
    val contrastOnWhite: Double,
    val passAAWhite: Boolean,
    val passAAAWhite: Boolean,
    val contrastOnBlack: Double,
    val passAABlack: Boolean,
    val passAAABlack: Boolean
)

object ColorPaletteEngine {

    fun hexFromRgb(r: Int, g: Int, b: Int): String {
        return String.format("#%02X%02X%02X", r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255))
    }

    fun parseHex(hexStr: String): Color? {
        val clean = hexStr.trim().removePrefix("#")
        if (clean.length != 6 && clean.length != 8) return null
        return try {
            val colorInt = clean.toLong(16)
            if (clean.length == 6) {
                Color((0xFF000000 or colorInt).toInt())
            } else {
                Color(colorInt.toInt())
            }
        } catch (e: Exception) {
            null
        }
    }

    fun rgbToHsl(r: Int, g: Int, b: Int): FloatArray {
        val rf = r / 255f
        val gf = g / 255f
        val bf = b / 255f

        val max = max(rf, max(gf, bf))
        val min = min(rf, min(gf, bf))
        val delta = max - min

        var h = 0f
        var s = 0f
        val l = (max + min) / 2f

        if (delta != 0f) {
            s = if (l <= 0.5f) delta / (max + min) else delta / (2f - max - min)
            h = when (max) {
                rf -> ((gf - bf) / delta) + (if (gf < bf) 6f else 0f)
                gf -> ((bf - rf) / delta) + 2f
                else -> ((rf - gf) / delta) + 4f
            }
            h *= 60f
        }

        return floatArrayOf(h, s * 100f, l * 100f)
    }

    fun hslToColor(h: Float, s: Float, l: Float): Color {
        val hue = (h % 360f + 360f) % 360f
        val sat = s.coerceIn(0f, 100f) / 100f
        val light = l.coerceIn(0f, 100f) / 100f

        val c = (1f - kotlin.math.abs(2f * light - 1f)) * sat
        val x = c * (1f - kotlin.math.abs((hue / 60f) % 2f - 1f))
        val m = light - c / 2f

        val (rPrime, gPrime, bPrime) = when ((hue / 60f).toInt()) {
            0 -> Triple(c, x, 0f)
            1 -> Triple(x, c, 0f)
            2 -> Triple(0f, c, x)
            3 -> Triple(0f, x, c)
            4 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        val r = ((rPrime + m) * 255f).roundToInt().coerceIn(0, 255)
        val g = ((gPrime + m) * 255f).roundToInt().coerceIn(0, 255)
        val b = ((bPrime + m) * 255f).roundToInt().coerceIn(0, 255)

        return Color(r, g, b)
    }

    fun generateHarmonies(r: Int, g: Int, b: Int): List<ColorSwatch> {
        val hsl = rgbToHsl(r, g, b)
        val h = hsl[0]
        val s = hsl[1]
        val l = hsl[2]

        val swatches = mutableListOf<ColorSwatch>()

        fun makeSwatch(name: String, targetH: Float, targetS: Float, targetL: Float): ColorSwatch {
            val col = hslToColor(targetH, targetS, targetL)
            val red = (col.red * 255).roundToInt()
            val green = (col.green * 255).roundToInt()
            val blue = (col.blue * 255).roundToInt()
            return ColorSwatch(
                name = name,
                color = col,
                hex = hexFromRgb(red, green, blue),
                rgb = "rgb($red, $green, $blue)",
                hsl = "hsl(${targetH.toInt()}°, ${targetS.toInt()}%, ${targetL.toInt()}%)"
            )
        }

        swatches.add(makeSwatch("Base Color", h, s, l))
        swatches.add(makeSwatch("Complementary", (h + 180f) % 360f, s, l))
        swatches.add(makeSwatch("Analogous (+30°)", (h + 30f) % 360f, s, l))
        swatches.add(makeSwatch("Analogous (-30°)", (h + 330f) % 360f, s, l))
        swatches.add(makeSwatch("Triadic (+120°)", (h + 120f) % 360f, s, l))
        swatches.add(makeSwatch("Triadic (+240°)", (h + 240f) % 360f, s, l))
        swatches.add(makeSwatch("Lighter (+20%)", h, s, (l + 20f).coerceAtMost(95f)))
        swatches.add(makeSwatch("Darker (-20%)", h, s, (l - 20f).coerceAtLeast(10f)))

        return swatches
    }

    // WCAG 2.1 relative luminance
    private fun relativeLuminance(r: Int, g: Int, b: Int): Double {
        fun channel(c: Int): Double {
            val norm = c / 255.0
            return if (norm <= 0.03928) norm / 12.92 else Math.pow((norm + 0.055) / 1.055, 2.4)
        }
        return 0.2126 * channel(r) + 0.7152 * channel(g) + 0.0722 * channel(b)
    }

    fun calculateContrast(r: Int, g: Int, b: Int): ContrastReport {
        val lum = relativeLuminance(r, g, b)
        val lumWhite = 1.0
        val lumBlack = 0.0

        val ratioWhite = (lumWhite + 0.05) / (lum + 0.05)
        val ratioBlack = (lum + 0.05) / (lumBlack + 0.05)

        val cw = if (ratioWhite < 1.0) 1.0 / ratioWhite else ratioWhite
        val cb = if (ratioBlack < 1.0) 1.0 / ratioBlack else ratioBlack

        return ContrastReport(
            contrastOnWhite = cw,
            passAAWhite = cw >= 4.5,
            passAAAWhite = cw >= 7.0,
            contrastOnBlack = cb,
            passAABlack = cb >= 4.5,
            passAAABlack = cb >= 7.0
        )
    }
}
