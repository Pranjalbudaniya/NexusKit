package com.nexuskit.app.feature.tools.jwt_decoder

import android.util.Base64
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class JwtStandardClaims(
    val issuer: String? = null,
    val subject: String? = null,
    val audience: String? = null,
    val expirationTime: Long? = null,
    val notBefore: Long? = null,
    val issuedAt: Long? = null,
    val jwtId: String? = null,
    val isExpired: Boolean? = null,
    val expiresStatusText: String? = null
)

data class JwtDecodeResult(
    val rawHeader: String,
    val rawPayload: String,
    val rawSignature: String,
    val prettyHeaderJson: String,
    val prettyPayloadJson: String,
    val algorithm: String?,
    val tokenType: String?,
    val claims: JwtStandardClaims
)

object JwtEngine {

    fun decodeJwt(token: String): Result<JwtDecodeResult> {
        return runCatching {
            val cleanToken = token.trim()
            val parts = cleanToken.split('.')
            if (parts.size < 2 || parts.size > 3) {
                throw IllegalArgumentException("Invalid JWT format: A valid JWT must contain 2 or 3 dot-separated segments.")
            }

            val headerB64 = parts[0]
            val payloadB64 = parts[1]
            val signatureB64 = if (parts.size == 3) parts[2] else ""

            val headerJsonStr = decodeBase64Url(headerB64)
            val payloadJsonStr = decodeBase64Url(payloadB64)

            val prettyHeader = formatJson(headerJsonStr)
            val prettyPayload = formatJson(payloadJsonStr)

            val headerObj = runCatching { JSONObject(headerJsonStr) }.getOrNull()
            val payloadObj = runCatching { JSONObject(payloadJsonStr) }.getOrNull()

            val alg = headerObj?.optString("alg")
            val typ = headerObj?.optString("typ")

            val nowSeconds = System.currentTimeMillis() / 1000L

            var expTime: Long? = null
            var isExpired: Boolean? = null
            var expStatusText: String? = null

            if (payloadObj != null && payloadObj.has("exp")) {
                val exp = payloadObj.optLong("exp")
                expTime = exp
                val diffSeconds = exp - nowSeconds
                if (diffSeconds < 0) {
                    isExpired = true
                    expStatusText = "Expired ${formatDuration(Math.abs(diffSeconds))} ago (${formatDate(exp)})"
                } else {
                    isExpired = false
                    expStatusText = "Expires in ${formatDuration(diffSeconds)} (${formatDate(exp)})"
                }
            }

            val claims = JwtStandardClaims(
                issuer = payloadObj?.optString("iss")?.takeIf { it.isNotBlank() },
                subject = payloadObj?.optString("sub")?.takeIf { it.isNotBlank() },
                audience = payloadObj?.optString("aud")?.takeIf { it.isNotBlank() },
                expirationTime = expTime,
                notBefore = payloadObj?.optLong("nbf")?.takeIf { it > 0 },
                issuedAt = payloadObj?.optLong("iat")?.takeIf { it > 0 },
                jwtId = payloadObj?.optString("jti")?.takeIf { it.isNotBlank() },
                isExpired = isExpired,
                expiresStatusText = expStatusText
            )

            JwtDecodeResult(
                rawHeader = headerB64,
                rawPayload = payloadB64,
                rawSignature = signatureB64,
                prettyHeaderJson = prettyHeader,
                prettyPayloadJson = prettyPayload,
                algorithm = alg,
                tokenType = typ,
                claims = claims
            )
        }
    }

    fun verifyHmacSignature(token: String, secret: String): Boolean {
        if (secret.isEmpty()) return false
        val parts = token.trim().split('.')
        if (parts.size != 3) return false

        val content = "${parts[0]}.${parts[1]}"
        val signatureB64 = parts[2]

        return runCatching {
            val mac = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
            mac.init(secretKey)
            val hash = mac.doFinal(content.toByteArray(StandardCharsets.UTF_8))
            val computedB64 = Base64.encodeToString(hash, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

            computedB64 == signatureB64.trimEnd('=')
        }.getOrDefault(false)
    }

    private fun decodeBase64Url(input: String): String {
        val decoded = Base64.decode(input, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        return String(decoded, StandardCharsets.UTF_8)
    }

    private fun formatJson(json: String): String {
        return runCatching {
            if (json.trim().startsWith("[")) {
                org.json.JSONArray(json).toString(2)
            } else {
                JSONObject(json).toString(2)
            }
        }.getOrDefault(json)
    }

    private fun formatDate(epochSeconds: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return sdf.format(Date(epochSeconds * 1000L))
    }

    private fun formatDuration(seconds: Long): String {
        val days = seconds / 86400
        val hours = (seconds % 86400) / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60

        return when {
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${mins}m"
            mins > 0 -> "${mins}m ${secs}s"
            else -> "${secs}s"
        }
    }
}
