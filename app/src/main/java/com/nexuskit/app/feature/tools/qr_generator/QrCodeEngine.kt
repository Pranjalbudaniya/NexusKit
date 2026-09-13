package com.nexuskit.app.feature.tools.qr_generator

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

enum class QrType(val displayName: String) {
    TEXT("Plain Text"),
    URL("Website URL"),
    WIFI("Wi-Fi"),
    EMAIL("Email"),
    PHONE("Phone / SMS")
}

object QrCodeEngine {

    fun generateQrBitmap(
        content: String,
        size: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        errorCorrection: ErrorCorrectionLevel = ErrorCorrectionLevel.M
    ): Bitmap? {
        if (content.isBlank()) return null
        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.ERROR_CORRECTION, errorCorrection)
                put(EncodeHintType.MARGIN, 1)
            }

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) foregroundColor else backgroundColor
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    fun buildWifiString(ssid: String, password: String, securityType: String = "WPA", isHidden: Boolean = false): String {
        val sec = if (password.isEmpty()) "nopass" else securityType
        val h = if (isHidden) "true" else "false"
        return "WIFI:T:$sec;S:$ssid;P:$password;H:$h;;"
    }

    fun buildEmailString(email: String, subject: String = "", body: String = ""): String {
        return "mailto:$email?subject=${java.net.URLEncoder.encode(subject, "UTF-8")}&body=${java.net.URLEncoder.encode(body, "UTF-8")}"
    }

    fun buildPhoneString(phone: String): String {
        return "tel:$phone"
    }

    fun buildSmsString(phone: String, message: String = ""): String {
        return "smsto:$phone:$message"
    }
}
