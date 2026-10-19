package com.nexuskit.app.feature.tools.barcode_generator

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import java.util.EnumMap

enum class SupportedBarcodeFormat(
    val title: String,
    val zxingFormat: BarcodeFormat,
    val description: String,
    val sample: String
) {
    CODE_128("Code 128", BarcodeFormat.CODE_128, "Alphanumeric universal barcode", "NEXUS-128-KIT"),
    EAN_13("EAN-13", BarcodeFormat.EAN_13, "13-digit retail barcode (auto-checksum)", "590123412345"),
    EAN_8("EAN-8", BarcodeFormat.EAN_8, "8-digit compact retail barcode", "9638507"),
    UPC_A("UPC-A", BarcodeFormat.UPC_A, "12-digit standard US retail barcode", "01234567890"),
    CODE_39("Code 39", BarcodeFormat.CODE_39, "Classic industrial barcode", "NEXUS39"),
    ITF("ITF (Interleaved 2 of 5)", BarcodeFormat.ITF, "Even number of digits for shipping/cartons", "1234567890"),
    CODABAR("Codabar", BarcodeFormat.CODABAR, "Libraries and blood banks (A-D, 0-9)", "A12345678B"),
    PDF_417("PDF417", BarcodeFormat.PDF_417, "2D stacked barcode used on IDs and boarding passes", "ID:NX-998234-A")
}

data class BarcodeValidationResult(
    val isValid: Boolean,
    val normalizedContent: String,
    val errorMessage: String? = null
)

object BarcodeEngine {

    fun validate(content: String, format: SupportedBarcodeFormat): BarcodeValidationResult {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {
            return BarcodeValidationResult(false, "", "Content cannot be empty")
        }

        return when (format) {
            SupportedBarcodeFormat.EAN_13 -> {
                if (!trimmed.all { it.isDigit() }) {
                    return BarcodeValidationResult(false, trimmed, "EAN-13 requires numbers only")
                }
                if (trimmed.length == 12) {
                    val check = calculateEan13Checksum(trimmed)
                    BarcodeValidationResult(true, "$trimmed$check")
                } else if (trimmed.length == 13) {
                    val expected = calculateEan13Checksum(trimmed.substring(0, 12))
                    if (trimmed.last().digitToInt() != expected) {
                        BarcodeValidationResult(false, trimmed, "Invalid checksum digit (expected $expected)")
                    } else {
                        BarcodeValidationResult(true, trimmed)
                    }
                } else {
                    BarcodeValidationResult(false, trimmed, "EAN-13 requires 12 or 13 digits (found ${trimmed.length})")
                }
            }
            SupportedBarcodeFormat.EAN_8 -> {
                if (!trimmed.all { it.isDigit() }) {
                    return BarcodeValidationResult(false, trimmed, "EAN-8 requires numbers only")
                }
                if (trimmed.length == 7) {
                    val check = calculateEan8Checksum(trimmed)
                    BarcodeValidationResult(true, "$trimmed$check")
                } else if (trimmed.length == 8) {
                    val expected = calculateEan8Checksum(trimmed.substring(0, 7))
                    if (trimmed.last().digitToInt() != expected) {
                        BarcodeValidationResult(false, trimmed, "Invalid checksum digit (expected $expected)")
                    } else {
                        BarcodeValidationResult(true, trimmed)
                    }
                } else {
                    BarcodeValidationResult(false, trimmed, "EAN-8 requires 7 or 8 digits (found ${trimmed.length})")
                }
            }
            SupportedBarcodeFormat.UPC_A -> {
                if (!trimmed.all { it.isDigit() }) {
                    return BarcodeValidationResult(false, trimmed, "UPC-A requires numbers only")
                }
                if (trimmed.length == 11) {
                    val check = calculateUpcAChecksum(trimmed)
                    BarcodeValidationResult(true, "$trimmed$check")
                } else if (trimmed.length == 12) {
                    val expected = calculateUpcAChecksum(trimmed.substring(0, 11))
                    if (trimmed.last().digitToInt() != expected) {
                        BarcodeValidationResult(false, trimmed, "Invalid checksum digit (expected $expected)")
                    } else {
                        BarcodeValidationResult(true, trimmed)
                    }
                } else {
                    BarcodeValidationResult(false, trimmed, "UPC-A requires 11 or 12 digits (found ${trimmed.length})")
                }
            }
            SupportedBarcodeFormat.ITF -> {
                if (!trimmed.all { it.isDigit() }) {
                    return BarcodeValidationResult(false, trimmed, "ITF requires digits only")
                }
                if (trimmed.length % 2 != 0) {
                    return BarcodeValidationResult(false, trimmed, "ITF requires an even number of digits (found ${trimmed.length})")
                }
                BarcodeValidationResult(true, trimmed)
            }
            SupportedBarcodeFormat.CODE_39 -> {
                val valid39 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ -.$/+%"
                val upper = trimmed.uppercase()
                if (!upper.all { it in valid39 }) {
                    return BarcodeValidationResult(false, upper, "Code 39 only accepts A-Z, 0-9 and -.$/+%")
                }
                BarcodeValidationResult(true, upper)
            }
            SupportedBarcodeFormat.CODABAR -> {
                val validCodabar = "0123456789-$:/.+ABCD"
                val upper = trimmed.uppercase()
                if (!upper.all { it in validCodabar }) {
                    return BarcodeValidationResult(false, upper, "Codabar only accepts 0-9, -$:/+ and delimiters A-D")
                }
                BarcodeValidationResult(true, upper)
            }
            SupportedBarcodeFormat.CODE_128, SupportedBarcodeFormat.PDF_417 -> {
                BarcodeValidationResult(true, trimmed)
            }
        }
    }

    fun generateBarcodeBitmap(
        content: String,
        format: SupportedBarcodeFormat,
        width: Int = 800,
        height: Int = 300,
        showTextLabel: Boolean = true
    ): Result<Bitmap> = runCatching {
        val validation = validate(content, format)
        if (!validation.isValid) {
            throw IllegalArgumentException(validation.errorMessage ?: "Validation failed")
        }

        val finalContent = validation.normalizedContent

        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.MARGIN, 10)
        }

        val bitMatrix = MultiFormatWriter().encode(
            finalContent,
            format.zxingFormat,
            width,
            if (showTextLabel && format != SupportedBarcodeFormat.PDF_417) height - 60 else height,
            hints
        )

        val matrixWidth = bitMatrix.width
        val matrixHeight = bitMatrix.height

        val totalHeight = if (showTextLabel && format != SupportedBarcodeFormat.PDF_417) matrixHeight + 60 else matrixHeight
        val bitmap = Bitmap.createBitmap(matrixWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val barPaint = Paint().apply {
            color = Color.BLACK
            isAntiAlias = false
        }

        for (x in 0 until matrixWidth) {
            for (y in 0 until matrixHeight) {
                if (bitMatrix.get(x, y)) {
                    canvas.drawPoint(x.toFloat(), y.toFloat(), barPaint)
                }
            }
        }

        if (showTextLabel && format != SupportedBarcodeFormat.PDF_417) {
            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 32f
                isAntiAlias = true
                typeface = Typeface.MONOSPACE
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                finalContent,
                (matrixWidth / 2).toFloat(),
                (matrixHeight + 42).toFloat(),
                textPaint
            )
        }

        bitmap
    }

    private fun calculateEan13Checksum(twelveDigits: String): Int {
        var sum = 0
        for (i in 0 until 12) {
            val d = twelveDigits[i].digitToInt()
            sum += if (i % 2 == 0) d else d * 3
        }
        val mod = sum % 10
        return if (mod == 0) 0 else 10 - mod
    }

    private fun calculateEan8Checksum(sevenDigits: String): Int {
        var sum = 0
        for (i in 0 until 7) {
            val d = sevenDigits[i].digitToInt()
            sum += if (i % 2 == 0) d * 3 else d
        }
        val mod = sum % 10
        return if (mod == 0) 0 else 10 - mod
    }

    private fun calculateUpcAChecksum(elevenDigits: String): Int {
        var sum = 0
        for (i in 0 until 11) {
            val d = elevenDigits[i].digitToInt()
            sum += if (i % 2 == 0) d * 3 else d
        }
        val mod = sum % 10
        return if (mod == 0) 0 else 10 - mod
    }
}
