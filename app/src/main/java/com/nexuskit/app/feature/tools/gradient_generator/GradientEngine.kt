package com.nexuskit.app.feature.tools.gradient_generator

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.RadialGradient
import android.graphics.SweepGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

enum class GradientType(val label: String) {
    LINEAR("Linear"),
    RADIAL("Radial"),
    SWEEP("Sweep")
}

data class GradientPreset(
    val name: String,
    val colors: List<Color>,
    val angle: Float = 135f
)

object GradientEngine {

    val PRESETS = listOf(
        GradientPreset("Sunset Glow", listOf(Color(0xFFFF512F), Color(0xFFDD2476)), 135f),
        GradientPreset("Cyberpunk", listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121)), 90f),
        GradientPreset("Aurora Borealis", listOf(Color(0xFF00C9FF), Color(0xFF92FE9D)), 120f),
        GradientPreset("Midnight Blue", listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)), 180f),
        GradientPreset("Neon Purple", listOf(Color(0xFF7F00FF), Color(0xFFE100FF)), 45f),
        GradientPreset("Ocean Breeze", listOf(Color(0xFF2E3192), Color(0xFF1BFFFF)), 135f),
        GradientPreset("Ember Fire", listOf(Color(0xFFFF416C), Color(0xFFFF4B2B)), 90f),
        GradientPreset("Emerald Mint", listOf(Color(0xFF0575E6), Color(0xFF00F260)), 135f),
        GradientPreset("Solar Flare", listOf(Color(0xFFF12711), Color(0xFFF5AF19)), 45f),
        GradientPreset("Tokyo Synth", listOf(Color(0xFF4A00E0), Color(0xFF8E2DE2)), 135f),
        GradientPreset("Rose Gold", listOf(Color(0xFFF4C4F3), Color(0xFFFC67FA)), 120f),
        GradientPreset("Deep Space", listOf(Color(0xFF000428), Color(0xFF004E92)), 180f)
    )

    fun generateCss(type: GradientType, colors: List<Color>, angleDegrees: Float): String {
        val hexList = colors.joinToString(", ") { toHexString(it) }
        return when (type) {
            GradientType.LINEAR -> "background: linear-gradient(${angleDegrees.toInt()}deg, $hexList);"
            GradientType.RADIAL -> "background: radial-gradient(circle, $hexList);"
            GradientType.SWEEP -> "background: conic-gradient(from 0deg, $hexList);"
        }
    }

    fun generateComposeCode(type: GradientType, colors: List<Color>, angleDegrees: Float): String {
        val composeColors = colors.joinToString(", ") { "Color(0x${toArgbHexString(it)})" }
        return when (type) {
            GradientType.LINEAR -> "Brush.linearGradient(colors = listOf($composeColors))"
            GradientType.RADIAL -> "Brush.radialGradient(colors = listOf($composeColors))"
            GradientType.SWEEP -> "Brush.sweepGradient(colors = listOf($composeColors))"
        }
    }

    fun generateXmlShape(type: GradientType, colors: List<Color>, angleDegrees: Float): String {
        val start = toHexString(colors.first())
        val end = toHexString(colors.last())
        val center = if (colors.size > 2) " android:centerColor=\"${toHexString(colors[1])}\"" else ""
        return """<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <gradient
        android:type="${type.name.lowercase()}"
        android:angle="${angleDegrees.toInt()}"
        android:startColor="$start"$center
        android:endColor="$end" />
</shape>""".trimIndent()
    }

    fun renderWallpaperBitmap(
        type: GradientType,
        colors: List<Color>,
        angleDegrees: Float,
        width: Int = 1080,
        height: Int = 2400
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val intColors = colors.map { toAndroidColor(it) }.toIntArray()
        val rad = Math.toRadians(angleDegrees.toDouble())

        val x0 = (width / 2.0 - cos(rad) * width / 2.0).toFloat()
        val y0 = (height / 2.0 - sin(rad) * height / 2.0).toFloat()
        val x1 = (width / 2.0 + cos(rad) * width / 2.0).toFloat()
        val y1 = (height / 2.0 + sin(rad) * height / 2.0).toFloat()

        val shader: Shader = when (type) {
            GradientType.LINEAR -> LinearGradient(x0, y0, x1, y1, intColors, null, Shader.TileMode.CLAMP)
            GradientType.RADIAL -> RadialGradient(
                (width / 2).toFloat(),
                (height / 2).toFloat(),
                (Math.min(width, height) / 1.5).toFloat(),
                intColors,
                null,
                Shader.TileMode.CLAMP
            )
            GradientType.SWEEP -> SweepGradient((width / 2).toFloat(), (height / 2).toFloat(), intColors, null)
        }

        val paint = Paint().apply {
            this.shader = shader
            isDither = true
        }

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    fun toHexString(color: Color): String {
        val r = (color.red * 255).toInt().coerceIn(0, 255)
        val g = (color.green * 255).toInt().coerceIn(0, 255)
        val b = (color.blue * 255).toInt().coerceIn(0, 255)
        return String.format(java.util.Locale.US, "#%02X%02X%02X", r, g, b)
    }

    private fun toArgbHexString(color: Color): String {
        val a = (color.alpha * 255).toInt().coerceIn(0, 255)
        val r = (color.red * 255).toInt().coerceIn(0, 255)
        val g = (color.green * 255).toInt().coerceIn(0, 255)
        val b = (color.blue * 255).toInt().coerceIn(0, 255)
        return String.format(java.util.Locale.US, "%02X%02X%02X%02X", a, r, g, b)
    }

    fun toAndroidColor(color: Color): Int {
        val a = (color.alpha * 255).toInt().coerceIn(0, 255)
        val r = (color.red * 255).toInt().coerceIn(0, 255)
        val g = (color.green * 255).toInt().coerceIn(0, 255)
        val b = (color.blue * 255).toInt().coerceIn(0, 255)
        return android.graphics.Color.argb(a, r, g, b)
    }
}
