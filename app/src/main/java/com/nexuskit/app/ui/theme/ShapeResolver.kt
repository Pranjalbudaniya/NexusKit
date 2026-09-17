package com.nexuskit.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.nexuskit.app.domain.model.enums.CardShapeType
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

/**
 * Maps every CardShapeType to a Compose Shape.
 * Called from ToolCard, ToolPill, ToolIconItem, FavouritesRow, ShapePicker.
 *
 * Usage:
 *   val shape = userPreferences.cardShape.toComposeShape()
 *   Surface(shape = shape) { ... }
 */
fun CardShapeType.toComposeShape(): Shape = when (this) {

    // ── Rounded Rectangles ────────────────────────────────────────────────────
    CardShapeType.ROUNDED_EXTRA_SMALL -> RoundedCornerShape(4.dp)
    CardShapeType.ROUNDED_SMALL       -> RoundedCornerShape(8.dp)
    CardShapeType.ROUNDED_MEDIUM      -> RoundedCornerShape(12.dp)
    CardShapeType.ROUNDED_LARGE       -> RoundedCornerShape(20.dp)
    CardShapeType.ROUNDED_EXTRA_LARGE -> RoundedCornerShape(28.dp)
    CardShapeType.ROUNDED_FULL        -> RoundedCornerShape(50)

    // ── Circles & Ovals ───────────────────────────────────────────────────────
    CardShapeType.CIRCLE              -> CircleShape
    CardShapeType.SQUIRCLE            -> squircleShape()
    CardShapeType.CIRCLE_FLAT         -> ovalShape(widthRatio = 1.0f, heightRatio = 0.78f)
    CardShapeType.OVAL_VERTICAL       -> ovalShape(widthRatio = 0.75f, heightRatio = 1f)
    CardShapeType.OVAL_HORIZONTAL     -> ovalShape(widthRatio = 1f, heightRatio = 0.72f)
    CardShapeType.STADIUM             -> RoundedCornerShape(50)

    // ── Regular Polygons ──────────────────────────────────────────────────────
    CardShapeType.PENTAGON            -> regularPolygon(5, -PI.toFloat() / 2)
    CardShapeType.HEXAGON             -> regularPolygon(6, 0f)
    CardShapeType.HEXAGON_FLAT        -> regularPolygon(6, -PI.toFloat() / 2)
    CardShapeType.HEPTAGON            -> regularPolygon(7, -PI.toFloat() / 2)
    CardShapeType.HEPTAGON_THIN       -> scaledPolygon(7, -PI.toFloat() / 2, scaleX = 0.78f, scaleY = 1f)
    CardShapeType.DIAMOND             -> regularPolygon(4, 0f)
    CardShapeType.THIN_DIAMOND        -> stretchedDiamond(0.52f)
    CardShapeType.THIN_OCTAGON        -> scaledPolygon(8, (PI / 8).toFloat(), scaleX = 0.78f, scaleY = 1f)

    // ── Stars & Scallops ──────────────────────────────────────────────────────
    CardShapeType.SCALLOP_4           -> scallop(4, depth = 0.15f)
    CardShapeType.SCALLOP_8           -> scallop(8, depth = 0.12f)
    CardShapeType.SCALLOP_12          -> scallop(12, depth = 0.10f)
    CardShapeType.SCALLOP_CIRCLE      -> scallop(16, depth = 0.08f)
    CardShapeType.SCALLOP_ROUND       -> scallop(8, depth = 0.07f)
    CardShapeType.STAR_4              -> starShape(4, innerRatio = 0.45f)
    CardShapeType.STAR_8              -> starShape(8, innerRatio = 0.55f)
    CardShapeType.STARBURST_SHARP     -> starShape(12, innerRatio = 0.45f)
    CardShapeType.STARBURST_ROUND     -> starShape(12, innerRatio = 0.60f)
    CardShapeType.SUNFLOWER           -> starShape(20, innerRatio = 0.85f)
    CardShapeType.CROSS_PIXEL         -> pixelCrossShape()

    // ── Floral ────────────────────────────────────────────────────────────────
    CardShapeType.FLOWER_4            -> polarFlower(lobes = 4, baseRatio = 0.68f, amplitude = 0.28f, power = 2.0f, rotation = (PI / 4).toFloat())
    CardShapeType.FLOWER_ROUND        -> polarFlower(lobes = 5, baseRatio = 0.74f, amplitude = 0.22f, power = 1.2f)
    CardShapeType.CLOVER              -> polarFlower(lobes = 4, baseRatio = 0.70f, amplitude = 0.26f, power = 1.6f, rotation = 0f)
    CardShapeType.CLOVER_ROUND        -> polarFlower(lobes = 4, baseRatio = 0.76f, amplitude = 0.20f, power = 1.2f, rotation = 0f)
    CardShapeType.LOTUS               -> polarFlower(lobes = 8, baseRatio = 0.70f, amplitude = 0.26f, power = 1.4f)
    CardShapeType.SAKURA              -> polarFlower(lobes = 5, baseRatio = 0.72f, amplitude = 0.24f, power = 1.5f)
    CardShapeType.CLOUD               -> cloudShape()
    CardShapeType.SPEECH_BUBBLE       -> speechBubbleShape()

    // ── Special Silhouettes ───────────────────────────────────────────────────
    CardShapeType.HEART               -> heartShape()
    CardShapeType.HEART_SHARP         -> heartShape(sharpBottom = true)
    CardShapeType.TEARDROP            -> teardropShape()
    CardShapeType.ARCH                -> archShape()
    CardShapeType.TOMBSTONE           -> tombstoneShape()
    CardShapeType.RECT_BOTTOM_ROUNDED -> RoundedCornerShape(
        topStart = 0.dp, topEnd = 0.dp,
        bottomStart = 20.dp, bottomEnd = 20.dp
    )
    CardShapeType.TICKET              -> ticketShape()
    CardShapeType.BANNER              -> bannerShape()
    CardShapeType.BLOB                -> blobShape()
    CardShapeType.HOURGLASS           -> hourglassShape()
    CardShapeType.SLASH               -> slashShape()
    CardShapeType.CIRCLE_DOTTED       -> CircleShape       // same clip, dots added in renderer
    CardShapeType.PLUS                -> plusShape(armRatio = 0.35f)

    // ── No shape ──────────────────────────────────────────────────────────────
    CardShapeType.NONE                -> RoundedCornerShape(0.dp)
}

// ═══════════════════════════════════════════════════════════════════════════════
// SHAPE BUILDER HELPERS
// All helpers return Shape. Use GenericShape for custom paths.
// Path coordinates are normalised to [0, size.width] × [0, size.height].
// ═══════════════════════════════════════════════════════════════════════════════

/** Regular convex polygon with [sides] sides, rotated by [startAngle] radians. */
private fun regularPolygon(sides: Int, startAngle: Float = 0f): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r  = min(cx, cy)
            val path = Path()
            for (i in 0 until sides) {
                val angle = startAngle + (2 * PI * i / sides).toFloat()
                val x = cx + r * cos(angle)
                val y = cy + r * sin(angle)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            return Outline.Generic(path)
        }
    }

/** Star / burst with [points] outer points and [innerRatio] inner radius fraction. */
private fun starShape(points: Int, innerRatio: Float = 0.5f): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val outerR = min(cx, cy)
            val innerR = outerR * innerRatio
            val path = Path()
            for (i in 0 until points * 2) {
                val angle = (PI * i / points - PI / 2).toFloat()
                val r = if (i % 2 == 0) outerR else innerR
                val x = cx + r * cos(angle)
                val y = cy + r * sin(angle)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            return Outline.Generic(path)
        }
    }

/** Scallop — circle with sinusoidal bumpy edge. */
private fun scallop(bumps: Int, depth: Float = 0.12f): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val baseR = min(cx, cy)
            val steps = bumps * 24
            val path = Path()
            for (i in 0..steps) {
                val angle = (2 * PI * i / steps).toFloat()
                val bumpFactor = (1f - depth * ((1f - cos(bumps * angle)) / 2f))
                val r = baseR * bumpFactor
                val x = cx + r * cos(angle)
                val y = cy + r * sin(angle)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            return Outline.Generic(path)
        }
    }

/** Superellipse / squircle — smoother than a rounded rect, rounder than a circle. */
private fun squircleShape(exponent: Float = 4f): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = min(cx, cy)
            val steps = 128
            val path = Path()
            for (i in 0..steps) {
                val angle = (2 * PI * i / steps).toFloat()
                val cosA = cos(angle)
                val sinA = sin(angle)
                val x = cx + r * (abs(cosA).pow(2f / exponent)) * if (cosA >= 0) 1f else -1f
                val y = cy + r * (abs(sinA).pow(2f / exponent)) * if (sinA >= 0) 1f else -1f
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            return Outline.Generic(path)
        }
    }

/** Ellipse with configurable width/height ratios. */
private fun ovalShape(widthRatio: Float, heightRatio: Float): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val rx = min(cx, cy) * widthRatio
            val ry = min(cx, cy) * heightRatio
            val path = Path()
            path.addOval(Rect(cx - rx, cy - ry, cx + rx, cy + ry))
            return Outline.Generic(path)
        }
    }

/** Regular convex polygon with sides, optionally scaled on X or Y axis. */
private fun scaledPolygon(sides: Int, startAngle: Float = 0f, scaleX: Float = 1f, scaleY: Float = 1f): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r  = min(cx, cy)
            val path = Path()
            for (i in 0 until sides) {
                val angle = startAngle + (2 * PI * i / sides).toFloat()
                val x = cx + r * scaleX * cos(angle)
                val y = cy + r * scaleY * sin(angle)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            return Outline.Generic(path)
        }
    }

/** Diamond rotated and optionally stretched. */
private fun stretchedDiamond(widthRatio: Float = 0.52f): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val rx = min(cx, cy) * widthRatio
            val ry = min(cx, cy)
            val path = Path().apply {
                moveTo(cx, cy - ry)
                lineTo(cx + rx, cy)
                lineTo(cx, cy + ry)
                lineTo(cx - rx, cy)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Plus / cross shape. [armRatio] is the arm width as fraction of size. */
private fun plusShape(armRatio: Float = 0.35f): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val hw = w * armRatio / 2
            val hh = h * armRatio / 2
            val cx = w / 2f
            val cy = h / 2f
            val path = Path().apply {
                moveTo(cx - hw, 0f);      lineTo(cx + hw, 0f)
                lineTo(cx + hw, cy - hh); lineTo(w, cy - hh)
                lineTo(w, cy + hh);       lineTo(cx + hw, cy + hh)
                lineTo(cx + hw, h);       lineTo(cx - hw, h)
                lineTo(cx - hw, cy + hh); lineTo(0f, cy + hh)
                lineTo(0f, cy - hh);      lineTo(cx - hw, cy - hh)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Pixelated stepped cross (8-bit style). */
private fun pixelCrossShape(): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val u = w / 6f
            val v = h / 6f
            val path = Path().apply {
                moveTo(2 * u, 0f)
                lineTo(4 * u, 0f)
                lineTo(4 * u, 2 * v)
                lineTo(6 * u, 2 * v)
                lineTo(6 * u, 4 * v)
                lineTo(4 * u, 4 * v)
                lineTo(4 * u, 6 * v)
                lineTo(2 * u, 6 * v)
                lineTo(2 * u, 4 * v)
                lineTo(0f, 4 * v)
                lineTo(0f, 2 * v)
                lineTo(2 * u, 2 * v)
                close()
            }
            return Outline.Generic(path)
        }
    }

/**
 * Polar flower / clover curve — smooth lobes arranged around circle,
 * guaranteed to stay strictly within [0..w] × [0..h].
 */
private fun polarFlower(
    lobes: Int,
    baseRatio: Float = 0.72f,
    amplitude: Float = 0.24f,
    power: Float = 1.0f,
    rotation: Float = -PI.toFloat() / 2
): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val maxR = min(cx, cy) * 0.96f
            val steps = lobes * 32
            val path = Path()
            for (i in 0..steps) {
                val theta = (2 * PI * i / steps).toFloat()
                val wave = cos(lobes * (theta - rotation))
                val normalizedWave = if (power != 1.0f) {
                    val sign = if (wave >= 0) 1f else -1f
                    sign * abs(wave).pow(power)
                } else wave
                val r = maxR * (baseRatio + amplitude * normalizedWave)
                val x = cx + r * cos(theta)
                val y = cy + r * sin(theta)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            return Outline.Generic(path)
        }
    }

/** Cloud — fluffy balanced lobes on top, flat rounded bottom. */
private fun cloudShape(): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(w * 0.2f, h * 0.82f)
                lineTo(w * 0.8f, h * 0.82f)
                cubicTo(w * 0.96f, h * 0.82f, w, h * 0.68f, w * 0.94f, h * 0.56f)
                cubicTo(w * 0.96f, h * 0.38f, w * 0.84f, h * 0.30f, w * 0.70f, h * 0.34f)
                cubicTo(w * 0.66f, h * 0.14f, w * 0.38f, h * 0.14f, w * 0.32f, h * 0.34f)
                cubicTo(w * 0.18f, h * 0.30f, w * 0.04f, h * 0.42f, w * 0.06f, h * 0.58f)
                cubicTo(w * 0.04f, h * 0.72f, w * 0.10f, h * 0.82f, w * 0.2f, h * 0.82f)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Speech bubble — rounded rect with a small triangular tail at bottom left. */
private fun speechBubbleShape(): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val r = min(w, h) * 0.15f
            val tailH = h * 0.15f
            val bodyH = h - tailH
            val path = Path().apply {
                moveTo(r, 0f)
                lineTo(w - r, 0f); cubicTo(w, 0f, w, 0f, w, r)
                lineTo(w, bodyH - r); cubicTo(w, bodyH, w, bodyH, w - r, bodyH)
                lineTo(w * 0.35f, bodyH)
                lineTo(w * 0.15f, h)
                lineTo(w * 0.2f, bodyH)
                lineTo(r, bodyH); cubicTo(0f, bodyH, 0f, bodyH, 0f, bodyH - r)
                lineTo(0f, r); cubicTo(0f, 0f, 0f, 0f, r, 0f)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Heart — two cubic bezier lobes meeting at a bottom point. */
private fun heartShape(sharpBottom: Boolean = false): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val bY = if (sharpBottom) h * 0.98f else h * 0.88f
            val path = Path().apply {
                moveTo(w * 0.5f, h * 0.26f)
                // Right lobe
                cubicTo(w * 0.55f, h * 0.05f, w * 0.98f, h * 0.08f, w * 0.98f, h * 0.36f)
                cubicTo(w * 0.98f, h * 0.60f, w * 0.74f, h * 0.76f, w * 0.5f, bY)
                // Left lobe
                cubicTo(w * 0.26f, h * 0.76f, w * 0.02f, h * 0.60f, w * 0.02f, h * 0.36f)
                cubicTo(w * 0.02f, h * 0.08f, w * 0.45f, h * 0.05f, w * 0.5f, h * 0.26f)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Teardrop — rounded top + pointed bottom, continuous smooth contour. */
private fun teardropShape(): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(w * 0.5f, h * 0.98f)
                cubicTo(w * 0.06f, h * 0.72f, 0f, h * 0.44f, 0f, h * 0.32f)
                cubicTo(0f, h * 0.05f, w * 0.22f, 0f, w * 0.5f, 0f)
                cubicTo(w * 0.78f, 0f, w, h * 0.05f, w, h * 0.32f)
                cubicTo(w, h * 0.44f, w * 0.94f, h * 0.72f, w * 0.5f, h * 0.98f)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Arch — rectangle with a semicircular top, continuous smooth contour. */
private fun archShape(): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(0f, w * 0.5f)
                cubicTo(0f, w * 0.22f, w * 0.22f, 0f, w * 0.5f, 0f)
                cubicTo(w * 0.78f, 0f, w, w * 0.22f, w, w * 0.5f)
                lineTo(w, h)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Tombstone — arch with taller body, continuous smooth contour. */
private fun tombstoneShape(): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(0f, h * 0.46f)
                cubicTo(0f, h * 0.16f, w * 0.22f, 0f, w * 0.5f, 0f)
                cubicTo(w * 0.78f, 0f, w, h * 0.16f, w, h * 0.46f)
                lineTo(w, h)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Ticket — rounded rect with inward circular/cubic bites on left and right edges. */
private fun ticketShape(): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val r = min(w, h) * 0.14f
            val biteR = min(w, h) * 0.15f
            val cy = h / 2f
            val path = Path().apply {
                moveTo(r, 0f)
                lineTo(w - r, 0f)
                cubicTo(w, 0f, w, 0f, w, r)
                lineTo(w, cy - biteR)
                cubicTo(w - biteR * 1.3f, cy - biteR, w - biteR * 1.3f, cy + biteR, w, cy + biteR)
                lineTo(w, h - r)
                cubicTo(w, h, w, h, w - r, h)
                lineTo(r, h)
                cubicTo(0f, h, 0f, h, 0f, h - r)
                lineTo(0f, cy + biteR)
                cubicTo(biteR * 1.3f, cy + biteR, biteR * 1.3f, cy - biteR, 0f, cy - biteR)
                lineTo(0f, r)
                cubicTo(0f, 0f, 0f, 0f, r, 0f)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Banner — slightly concave top and bottom (pinched centre horizontally). */
private fun bannerShape(): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val dip = h * 0.08f
            val path = Path().apply {
                moveTo(0f, 0f)
                cubicTo(w * 0.25f, dip, w * 0.75f, dip, w, 0f)
                lineTo(w, h)
                cubicTo(w * 0.75f, h - dip, w * 0.25f, h - dip, 0f, h)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Organic blob — soft irregular 4-lobe shape. */
private fun blobShape(): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(w * 0.5f, 0f)
                cubicTo(w * 0.9f, 0f,      w,        h * 0.1f,  w,        h * 0.45f)
                cubicTo(w,        h * 0.8f, w * 0.9f, h,         w * 0.5f, h)
                cubicTo(w * 0.1f, h,        0f,        h * 0.8f,  0f,        h * 0.45f)
                cubicTo(0f,        h * 0.1f, w * 0.1f, 0f,         w * 0.5f, 0f)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Hourglass — two triangles meeting at the centre. */
private fun hourglassShape(): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val neck = w * 0.15f
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w / 2 + neck, h / 2)
                lineTo(w, h)
                lineTo(0f, h)
                lineTo(w / 2 - neck, h / 2)
                close()
            }
            return Outline.Generic(path)
        }
    }

/** Slash — rectangle cut diagonally. */
private fun slashShape(): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val w = size.width
            val h = size.height
            val cut = w * 0.2f
            val path = Path().apply {
                moveTo(cut, 0f)
                lineTo(w, 0f)
                lineTo(w - cut, h)
                lineTo(0f, h)
                close()
            }
            return Outline.Generic(path)
        }
    }
