package com.nexuskit.app.feature.tools.totp_authenticator

import android.net.Uri
import java.nio.ByteBuffer
import java.security.GeneralSecurityException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

data class TotpAccount(
    val id: String = java.util.UUID.randomUUID().toString(),
    val accountName: String,
    val issuer: String,
    val secretKey: String,
    val digits: Int = 6,
    val periodSeconds: Int = 30,
    val algorithm: String = "SHA1"
)

object TotpEngine {

    fun generateTotp(
        secretBase32: String,
        epochSeconds: Long = System.currentTimeMillis() / 1000L,
        period: Int = 30,
        digits: Int = 6,
        algorithm: String = "SHA1"
    ): Result<String> = runCatching {
        val cleanSecret = cleanBase32Secret(secretBase32)
        val keyBytes = decodeBase32(cleanSecret)
        val timeStep = epochSeconds / period

        val timeBytes = ByteBuffer.allocate(8).putLong(timeStep).array()
        val hmacAlgorithm = when (algorithm.uppercase()) {
            "SHA256", "HMACSHA256" -> "HmacSHA256"
            "SHA512", "HMACSHA512" -> "HmacSHA512"
            else -> "HmacSHA1"
        }

        val mac = Mac.getInstance(hmacAlgorithm)
        mac.init(SecretKeySpec(keyBytes, hmacAlgorithm))
        val hash = mac.doFinal(timeBytes)

        // Dynamic truncation (RFC 4226)
        val offset = hash.last().toInt() and 0x0F
        val binaryCode = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)

        val modulus = 10.0.pow(digits.toDouble()).toInt()
        val otpInt = binaryCode % modulus
        String.format(java.util.Locale.US, "%0${digits}d", otpInt)
    }

    fun parseOtpAuthUri(uriString: String): Result<TotpAccount> = runCatching {
        val clean = uriString.trim()
        if (!clean.startsWith("otpauth://", ignoreCase = true)) {
            throw IllegalArgumentException("Invalid URI scheme. Expected 'otpauth://'")
        }
        val withoutScheme = clean.substring(10)
        val qIdx = withoutScheme.indexOf('?')
        val path = if (qIdx != -1) withoutScheme.substring(0, qIdx) else withoutScheme
        val query = if (qIdx != -1) withoutScheme.substring(qIdx + 1) else ""

        val typeAndLabel = path.split('/', limit = 2)
        val type = typeAndLabel[0]
        if (!type.equals("totp", ignoreCase = true)) {
            throw IllegalArgumentException("Only 'totp' authentication type is supported.")
        }
        val label = if (typeAndLabel.size > 1) java.net.URLDecoder.decode(typeAndLabel[1], "UTF-8") else ""

        val queryParams = mutableMapOf<String, String>()
        if (query.isNotEmpty()) {
            query.split('&').forEach { param ->
                val pair = param.split('=', limit = 2)
                if (pair.isNotEmpty()) {
                    val k = pair[0].lowercase()
                    val v = if (pair.size > 1) java.net.URLDecoder.decode(pair[1], "UTF-8") else ""
                    queryParams[k] = v
                }
            }
        }

        var issuerParam = queryParams["issuer"] ?: ""
        var accountName = label
        if (label.contains(":")) {
            val parts = label.split(":", limit = 2)
            if (issuerParam.isEmpty()) issuerParam = parts[0].trim()
            accountName = parts[1].trim()
        }

        val secret = queryParams["secret"]
            ?: throw IllegalArgumentException("Missing required 'secret' query parameter.")

        val digits = queryParams["digits"]?.toIntOrNull() ?: 6
        val period = queryParams["period"]?.toIntOrNull() ?: 30
        val algorithm = queryParams["algorithm"] ?: "SHA1"

        TotpAccount(
            accountName = if (accountName.isNotBlank()) accountName else "2FA Account",
            issuer = if (issuerParam.isNotBlank()) issuerParam else "Authenticator",
            secretKey = cleanBase32Secret(secret),
            digits = digits,
            periodSeconds = period,
            algorithm = algorithm
        )
    }

    fun cleanBase32Secret(secret: String): String {
        return secret.trim()
            .replace("\\s+".toRegex(), "")
            .replace("-", "")
            .uppercase()
    }

    fun decodeBase32(base32: String): ByteArray {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val clean = base32.trimEnd('=')
        if (clean.isEmpty()) return ByteArray(0)

        val bytes = mutableListOf<Byte>()
        var buffer = 0
        var bitsLeft = 0

        for (c in clean) {
            val charVal = alphabet.indexOf(c)
            if (charVal < 0) {
                throw IllegalArgumentException("Invalid Base32 character: '$c'")
            }
            buffer = (buffer shl 5) or charVal
            bitsLeft += 5
            if (bitsLeft >= 8) {
                bitsLeft -= 8
                bytes.add(((buffer shr bitsLeft) and 0xFF).toByte())
            }
        }
        return bytes.toByteArray()
    }

    fun formatCodeForDisplay(code: String): String {
        return when (code.length) {
            6 -> "${code.substring(0, 3)} ${code.substring(3)}"
            8 -> "${code.substring(0, 4)} ${code.substring(4)}"
            else -> code
        }
    }
}
