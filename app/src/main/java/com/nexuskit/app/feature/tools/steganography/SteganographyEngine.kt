package com.nexuskit.app.feature.tools.steganography

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

data class StegoEncodeResult(
    val outputFile: File,
    val payloadSizeBytes: Int,
    val capacityBytes: Int,
    val isEncrypted: Boolean
)

data class StegoDecodeResult(
    val message: String,
    val isEncrypted: Boolean
)

object SteganographyEngine {

    private val MAGIC = byteArrayOf(0x53, 0x54, 0x45, 0x47) // "STEG"
    private const val FLAG_PLAIN: Byte = 0x00
    private const val FLAG_ENCRYPTED: Byte = 0x01

    fun calculateCapacityBytes(width: Int, height: Int): Int {
        val totalBits = width.toLong() * height.toLong() * 3L
        return (totalBits / 8L).toInt()
    }

    fun encodeMessage(
        context: Context,
        imageUri: Uri,
        message: String,
        password: String? = null
    ): Result<StegoEncodeResult> {
        return runCatching {
            if (message.isBlank()) {
                throw IllegalArgumentException("Secret message cannot be empty")
            }

            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw IllegalArgumentException("Could not read cover image")
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
                ?: throw IllegalArgumentException("Failed to decode cover image bitmap")

            val width = originalBitmap.width
            val height = originalBitmap.height
            val capacityBytes = calculateCapacityBytes(width, height)

            val isEncrypted = !password.isNullOrBlank()
            val rawMessageBytes = message.toByteArray(Charsets.UTF_8)

            val payloadBuffer: ByteArray
            val flag: Byte

            if (isEncrypted) {
                flag = FLAG_ENCRYPTED
                val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
                val iv = ByteArray(16).also { SecureRandom().nextBytes(it) }
                val cipherBytes = encryptAes(rawMessageBytes, password ?: "", salt, iv)

                // Buffer: MAGIC (4) + FLAG (1) + LEN (4) + SALT (16) + IV (16) + CIPHER
                val bb = ByteBuffer.allocate(4 + 1 + 4 + 16 + 16 + cipherBytes.size)
                bb.put(MAGIC)
                bb.put(flag)
                bb.putInt(cipherBytes.size)
                bb.put(salt)
                bb.put(iv)
                bb.put(cipherBytes)
                payloadBuffer = bb.array()
            } else {
                flag = FLAG_PLAIN
                // Buffer: MAGIC (4) + FLAG (1) + LEN (4) + PLAIN_BYTES
                val bb = ByteBuffer.allocate(4 + 1 + 4 + rawMessageBytes.size)
                bb.put(MAGIC)
                bb.put(flag)
                bb.putInt(rawMessageBytes.size)
                bb.put(rawMessageBytes)
                payloadBuffer = bb.array()
            }

            if (payloadBuffer.size > capacityBytes) {
                throw IllegalArgumentException("Message is too large (${payloadBuffer.size} bytes). Image capacity is only $capacityBytes bytes. Select a larger photo.")
            }

            // Create mutable copy of bitmap
            val stegoBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            var byteIdx = 0
            var bitIdx = 7 // Read bits from MSB to LSB of each byte

            val totalPayloadBits = payloadBuffer.size * 8
            var bitsWritten = 0

            outer@ for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitsWritten >= totalPayloadBits) break@outer

                    val pixel = stegoBitmap.getPixel(x, y)
                    val alpha = (pixel shr 24) and 0xFF
                    var r = (pixel shr 16) and 0xFF
                    var g = (pixel shr 8) and 0xFF
                    var b = pixel and 0xFF

                    // Channel R
                    if (bitsWritten < totalPayloadBits) {
                        val bit = (payloadBuffer[byteIdx].toInt() shr bitIdx) and 1
                        r = (r and 0xFE) or bit
                        bitsWritten++
                        bitIdx--
                        if (bitIdx < 0) {
                            bitIdx = 7
                            byteIdx++
                        }
                    }

                    // Channel G
                    if (bitsWritten < totalPayloadBits) {
                        val bit = (payloadBuffer[byteIdx].toInt() shr bitIdx) and 1
                        g = (g and 0xFE) or bit
                        bitsWritten++
                        bitIdx--
                        if (bitIdx < 0) {
                            bitIdx = 7
                            byteIdx++
                        }
                    }

                    // Channel B
                    if (bitsWritten < totalPayloadBits) {
                        val bit = (payloadBuffer[byteIdx].toInt() shr bitIdx) and 1
                        b = (b and 0xFE) or bit
                        bitsWritten++
                        bitIdx--
                        if (bitIdx < 0) {
                            bitIdx = 7
                            byteIdx++
                        }
                    }

                    val newPixel = (alpha shl 24) or (r shl 16) or (g shl 8) or b
                    stegoBitmap.setPixel(x, y, newPixel)
                }
            }

            // Export as lossless PNG file
            val stegoDir = File(context.cacheDir, "stego_images")
            if (!stegoDir.exists()) stegoDir.mkdirs()

            val stegoFile = File(stegoDir, "stego_${System.currentTimeMillis()}.png")
            FileOutputStream(stegoFile).use { out ->
                stegoBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }

            StegoEncodeResult(
                outputFile = stegoFile,
                payloadSizeBytes = payloadBuffer.size,
                capacityBytes = capacityBytes,
                isEncrypted = isEncrypted
            )
        }
    }

    fun decodeMessage(
        context: Context,
        imageUri: Uri,
        password: String? = null
    ): Result<StegoDecodeResult> {
        return runCatching {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw IllegalArgumentException("Could not read stego image")
            val bitmap = BitmapFactory.decodeStream(inputStream)
                ?: throw IllegalArgumentException("Failed to decode stego image bitmap")

            val width = bitmap.width
            val height = bitmap.height

            // Helper iterator to extract bits sequentially
            class BitReader {
                var x = 0
                var y = 0
                var channel = 0 // 0=R, 1=G, 2=B
                var curPixel = if (width > 0 && height > 0) bitmap.getPixel(0, 0) else 0

                fun nextBit(): Int {
                    if (y >= height) throw IllegalArgumentException("Premature end of image data")
                    val bit = when (channel) {
                        0 -> (curPixel shr 16) and 1
                        1 -> (curPixel shr 8) and 1
                        else -> curPixel and 1
                    }
                    channel++
                    if (channel > 2) {
                        channel = 0
                        x++
                        if (x >= width) {
                            x = 0
                            y++
                        }
                        if (y < height) {
                            curPixel = bitmap.getPixel(x, y)
                        }
                    }
                    return bit
                }

                fun nextByte(): Byte {
                    var b = 0
                    for (i in 7 downTo 0) {
                        b = b or (nextBit() shl i)
                    }
                    return b.toByte()
                }

                fun nextBytes(count: Int): ByteArray {
                    val arr = ByteArray(count)
                    for (i in 0 until count) {
                        arr[i] = nextByte()
                    }
                    return arr
                }

                fun nextInt(): Int {
                    val b0 = nextByte().toInt() and 0xFF
                    val b1 = nextByte().toInt() and 0xFF
                    val b2 = nextByte().toInt() and 0xFF
                    val b3 = nextByte().toInt() and 0xFF
                    return (b0 shl 24) or (b1 shl 16) or (b2 shl 8) or b3
                }
            }

            val reader = BitReader()

            // 1. Verify Magic
            val readMagic = reader.nextBytes(4)
            if (!readMagic.contentEquals(MAGIC)) {
                throw IllegalArgumentException("No hidden message detected. This image was not encoded with NexusKit Steganography.")
            }

            // 2. Read Flag & Payload Length
            val flag = reader.nextByte()
            val isEncrypted = (flag == FLAG_ENCRYPTED)
            val payloadLength = reader.nextInt()

            if (payloadLength <= 0 || payloadLength > width * height) {
                throw IllegalArgumentException("Corrupted steganography header or invalid payload length ($payloadLength).")
            }

            if (isEncrypted) {
                if (password.isNullOrBlank()) {
                    throw IllegalArgumentException("This hidden message is encrypted with a password. Please enter the password to decode it.")
                }
                val salt = reader.nextBytes(16)
                val iv = reader.nextBytes(16)
                val ciphertext = reader.nextBytes(payloadLength)

                val decryptedBytes = runCatching {
                    decryptAes(ciphertext, password, salt, iv)
                }.getOrElse {
                    throw IllegalArgumentException("Incorrect password or corrupted steganography payload.")
                }

                StegoDecodeResult(
                    message = String(decryptedBytes, Charsets.UTF_8),
                    isEncrypted = true
                )
            } else {
                val plainBytes = reader.nextBytes(payloadLength)
                StegoDecodeResult(
                    message = String(plainBytes, Charsets.UTF_8),
                    isEncrypted = false
                )
            }
        }
    }

    private fun encryptAes(data: ByteArray, pass: String, salt: ByteArray, iv: ByteArray): ByteArray {
        val keySpec = PBEKeySpec(pass.toCharArray(), salt, 10_000, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val secretKey = SecretKeySpec(factory.generateSecret(keySpec).encoded, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        return cipher.doFinal(data)
    }

    private fun decryptAes(data: ByteArray, pass: String, salt: ByteArray, iv: ByteArray): ByteArray {
        val keySpec = PBEKeySpec(pass.toCharArray(), salt, 10_000, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val secretKey = SecretKeySpec(factory.generateSecret(keySpec).encoded, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        return cipher.doFinal(data)
    }
}
