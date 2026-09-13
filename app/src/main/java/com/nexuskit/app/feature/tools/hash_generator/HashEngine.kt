package com.nexuskit.app.feature.tools.hash_generator

import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class HashResultItem(
    val algorithm: String,
    val hash: String,
    val bitLength: Int
)

object HashEngine {

    fun generateHashes(input: String, hmacKey: String = "", uppercase: Boolean = false): List<HashResultItem> {
        if (input.isEmpty()) return emptyList()

        val bytes = input.toByteArray(Charsets.UTF_8)
        val list = mutableListOf<HashResultItem>()

        // Message Digests
        val algorithms = listOf(
            "MD5" to 128,
            "SHA-1" to 160,
            "SHA-224" to 224,
            "SHA-256" to 256,
            "SHA-384" to 384,
            "SHA-512" to 512
        )

        for ((alg, bits) in algorithms) {
            try {
                val md = MessageDigest.getInstance(alg)
                val digest = md.digest(bytes)
                val hex = bytesToHex(digest, uppercase)
                list.add(HashResultItem(alg, hex, bits))
            } catch (e: Exception) {
                // Ignore unsupported
            }
        }

        // Base64 Encode
        try {
            val base64Encoded = Base64.getEncoder().encodeToString(bytes)
            list.add(HashResultItem("Base64", base64Encoded, bytes.size * 8))
        } catch (e: Exception) {
            // Ignore
        }

        // HMAC-SHA256 if key provided
        if (hmacKey.isNotEmpty()) {
            try {
                val secretKey = SecretKeySpec(hmacKey.toByteArray(Charsets.UTF_8), "HmacSHA256")
                val mac = Mac.getInstance("HmacSHA256")
                mac.init(secretKey)
                val hmacDigest = mac.doFinal(bytes)
                list.add(HashResultItem("HMAC-SHA256", bytesToHex(hmacDigest, uppercase), 256))
            } catch (e: Exception) {
                // Ignore
            }
        }

        return list
    }

    private fun bytesToHex(bytes: ByteArray, uppercase: Boolean): String {
        val sb = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            sb.append(String.format(if (uppercase) "%02X" else "%02x", b))
        }
        return sb.toString()
    }
}
