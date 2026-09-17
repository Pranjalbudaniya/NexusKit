package com.nexuskit.app.feature.tools.encryption

import android.util.Base64
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

enum class CryptoAlgorithm(val displayName: String, val requiresKey: Boolean, val keyHint: String) {
    AES("AES (128/256-bit)", true, "Secret password / key"),
    DES("DES", true, "8-character secret key"),
    CAESAR("Caesar Cipher", true, "Shift amount (1-25)"),
    VIGENERE("Vigenère Cipher", true, "Secret keyword (e.g. KEY)"),
    ROT13("ROT13", false, ""),
    BASE64("Base64", false, ""),
    HEX("Hexadecimal", false, ""),
    BINARY("Binary (8-bit)", false, ""),
    MORSE("Morse Code", false, ""),
    URL("URL Encode/Decode", false, "")
}

enum class CryptoMode(val label: String) {
    ENCRYPT("Encrypt / Encode"),
    DECRYPT("Decrypt / Decode")
}

data class EncryptionUiState(
    val mode: CryptoMode = CryptoMode.ENCRYPT,
    val algorithm: CryptoAlgorithm = CryptoAlgorithm.AES,
    val inputText: String = "",
    val keyInput: String = "secretKey123",
    val outputText: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class EncryptionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EncryptionUiState())
    val uiState: StateFlow<EncryptionUiState> = _uiState.asStateFlow()

    fun onModeSelected(mode: CryptoMode) {
        _uiState.update { it.copy(mode = mode) }
        process()
    }

    fun onAlgorithmSelected(algo: CryptoAlgorithm) {
        val defaultKey = when (algo) {
            CryptoAlgorithm.AES -> "secretKey123"
            CryptoAlgorithm.DES -> "8charKey"
            CryptoAlgorithm.CAESAR -> "3"
            CryptoAlgorithm.VIGENERE -> "NEXUS"
            else -> ""
        }
        _uiState.update { it.copy(algorithm = algo, keyInput = if (it.keyInput.isBlank()) defaultKey else it.keyInput) }
        process()
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
        process()
    }

    fun onKeyChanged(key: String) {
        _uiState.update { it.copy(keyInput = key) }
        process()
    }

    fun onClearInput() {
        _uiState.update { it.copy(inputText = "", outputText = "", errorMessage = null) }
    }

    fun onSwapInputOutput() {
        val current = _uiState.value
        val newMode = if (current.mode == CryptoMode.ENCRYPT) CryptoMode.DECRYPT else CryptoMode.ENCRYPT
        _uiState.update {
            it.copy(
                inputText = current.outputText,
                outputText = "",
                mode = newMode
            )
        }
        process()
    }

    private fun process() {
        val state = _uiState.value
        val text = state.inputText
        val key = state.keyInput
        val isEncrypt = state.mode == CryptoMode.ENCRYPT

        if (text.isEmpty()) {
            _uiState.update { it.copy(outputText = "", errorMessage = null) }
            return
        }

        try {
            val result = when (state.algorithm) {
                CryptoAlgorithm.AES -> if (isEncrypt) encryptAes(text, key) else decryptAes(text, key)
                CryptoAlgorithm.DES -> if (isEncrypt) encryptDes(text, key) else decryptDes(text, key)
                CryptoAlgorithm.CAESAR -> {
                    val shift = (key.filter { it.isDigit() }.toIntOrNull() ?: 3) % 26
                    if (isEncrypt) caesarCipher(text, shift) else caesarCipher(text, (26 - shift) % 26)
                }
                CryptoAlgorithm.VIGENERE -> if (isEncrypt) vigenere(text, key, true) else vigenere(text, key, false)
                CryptoAlgorithm.ROT13 -> rot13(text)
                CryptoAlgorithm.BASE64 -> if (isEncrypt) encodeBase64(text) else decodeBase64(text)
                CryptoAlgorithm.HEX -> if (isEncrypt) encodeHex(text) else decodeHex(text)
                CryptoAlgorithm.BINARY -> if (isEncrypt) encodeBinary(text) else decodeBinary(text)
                CryptoAlgorithm.MORSE -> if (isEncrypt) textToMorse(text) else morseToText(text)
                CryptoAlgorithm.URL -> if (isEncrypt) URLEncoder.encode(text, "UTF-8") else URLDecoder.decode(text, "UTF-8")
            }
            _uiState.update { it.copy(outputText = result, errorMessage = null) }
        } catch (e: Exception) {
            _uiState.update { it.copy(outputText = "", errorMessage = e.localizedMessage ?: "Operation failed. Check your input and key.") }
        }
    }

    // ── AES ───────────────────────────────────────────────────────────────────
    private fun encryptAes(text: String, password: String): String {
        val keyBytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(StandardCharsets.UTF_8))
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val iv = ByteArray(16) { 0 }
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        val encrypted = cipher.doFinal(text.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    private fun decryptAes(base64Text: String, password: String): String {
        val keyBytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(StandardCharsets.UTF_8))
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val iv = ByteArray(16) { 0 }
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        val decoded = Base64.decode(base64Text.trim(), Base64.DEFAULT)
        val decrypted = cipher.doFinal(decoded)
        return String(decrypted, StandardCharsets.UTF_8)
    }

    // ── DES ───────────────────────────────────────────────────────────────────
    private fun encryptDes(text: String, password: String): String {
        val keyBytes = MessageDigest.getInstance("MD5").digest(password.toByteArray(StandardCharsets.UTF_8)).copyOf(8)
        val secretKey = SecretKeySpec(keyBytes, "DES")
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encrypted = cipher.doFinal(text.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    private fun decryptDes(base64Text: String, password: String): String {
        val keyBytes = MessageDigest.getInstance("MD5").digest(password.toByteArray(StandardCharsets.UTF_8)).copyOf(8)
        val secretKey = SecretKeySpec(keyBytes, "DES")
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decoded = Base64.decode(base64Text.trim(), Base64.DEFAULT)
        val decrypted = cipher.doFinal(decoded)
        return String(decrypted, StandardCharsets.UTF_8)
    }

    // ── Caesar & ROT13 ────────────────────────────────────────────────────────
    private fun caesarCipher(text: String, shift: Int): String {
        return text.map { ch ->
            when {
                ch in 'a'..'z' -> 'a' + (ch - 'a' + shift) % 26
                ch in 'A'..'Z' -> 'A' + (ch - 'A' + shift) % 26
                else -> ch
            }
        }.joinToString("")
    }

    private fun rot13(text: String): String = caesarCipher(text, 13)

    // ── Vigenère ──────────────────────────────────────────────────────────────
    private fun vigenere(text: String, key: String, encrypt: Boolean): String {
        val cleanKey = key.filter { it.isLetter() }.uppercase()
        if (cleanKey.isEmpty()) return text
        var keyIdx = 0
        return text.map { ch ->
            if (ch.isLetter()) {
                val base = if (ch.isUpperCase()) 'A' else 'a'
                val kShift = cleanKey[keyIdx % cleanKey.length] - 'A'
                keyIdx++
                val shift = if (encrypt) kShift else (26 - kShift) % 26
                base + (ch - base + shift) % 26
            } else ch
        }.joinToString("")
    }

    // ── Base64 ────────────────────────────────────────────────────────────────
    private fun encodeBase64(text: String): String =
        Base64.encodeToString(text.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)

    private fun decodeBase64(text: String): String =
        String(Base64.decode(text.trim(), Base64.DEFAULT), StandardCharsets.UTF_8)

    // ── Hex ───────────────────────────────────────────────────────────────────
    private fun encodeHex(text: String): String =
        text.toByteArray(StandardCharsets.UTF_8).joinToString("") { "%02X".format(it) }

    private fun decodeHex(hex: String): String {
        val clean = hex.replace("\\s".toRegex(), "")
        require(clean.length % 2 == 0) { "Hex string length must be even" }
        val bytes = ByteArray(clean.length / 2) { i ->
            clean.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        return String(bytes, StandardCharsets.UTF_8)
    }

    // ── Binary ────────────────────────────────────────────────────────────────
    private fun encodeBinary(text: String): String =
        text.toByteArray(StandardCharsets.UTF_8).joinToString(" ") { b ->
            (0..7).map { bit -> ((b.toInt() shr (7 - bit)) and 1).toString() }.joinToString("")
        }

    private fun decodeBinary(binary: String): String {
        val tokens = binary.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        val bytes = tokens.map { token ->
            token.toInt(2).toByte()
        }.toByteArray()
        return String(bytes, StandardCharsets.UTF_8)
    }

    // ── Morse Code ────────────────────────────────────────────────────────────
    private val morseMap = mapOf(
        'A' to ".-",   'B' to "-...", 'C' to "-.-.", 'D' to "-..",
        'E' to ".",    'F' to "..-.", 'G' to "--.",  'H' to "....",
        'I' to "..",   'J' to ".---", 'K' to "-.-",  'L' to ".-..",
        'M' to "--",   'N' to "-.",   'O' to "---",  'P' to ".--.",
        'Q' to "--.-", 'R' to ".-.",  'S' to "...",  'T' to "-",
        'U' to "..-",  'V' to "...-", 'W' to ".--",  'X' to "-..-",
        'Y' to "-.--", 'Z' to "--..",
        '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-", '5' to ".....",
        '6' to "-....", '7' to "--...", '8' to "---..", '9' to "----.", '0' to "-----",
        ' ' to "/"
    )
    private val reverseMorseMap = morseMap.entries.associate { (k, v) -> v to k }

    private fun textToMorse(text: String): String =
        text.uppercase().map { morseMap[it] ?: "" }.filter { it.isNotEmpty() }.joinToString(" ")

    private fun morseToText(morse: String): String =
        morse.trim().split("\\s+".toRegex()).map { reverseMorseMap[it] ?: '?' }.joinToString("")
}
