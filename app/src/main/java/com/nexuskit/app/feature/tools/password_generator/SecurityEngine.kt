package com.nexuskit.app.feature.tools.password_generator

import java.security.SecureRandom
import java.util.UUID

enum class PasswordStrength(val label: String, val colorFraction: Float) {
    VERY_WEAK("Very Weak", 0.15f),
    WEAK("Weak", 0.35f),
    FAIR("Fair", 0.55f),
    STRONG("Strong", 0.8f),
    VERY_STRONG("Very Strong", 1.0f)
}

object SecurityEngine {

    private val random = SecureRandom()

    private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private const val DIGITS = "0123456789"
    private const val SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?"
    private val AMBIGUOUS = setOf('0', 'O', 'o', '1', 'l', 'I', '|', '`', '\'', '"')

    fun generatePassword(
        length: Int = 16,
        includeUpper: Boolean = true,
        includeLower: Boolean = true,
        includeDigits: Boolean = true,
        includeSymbols: Boolean = true,
        excludeAmbiguous: Boolean = false
    ): String {
        var pool = StringBuilder()
        if (includeUpper) pool.append(UPPERCASE)
        if (includeLower) pool.append(LOWERCASE)
        if (includeDigits) pool.append(DIGITS)
        if (includeSymbols) pool.append(SYMBOLS)

        if (pool.isEmpty()) pool.append(LOWERCASE) // Fallback

        var finalPool = pool.toString()
        if (excludeAmbiguous) {
            finalPool = finalPool.filterNot { it in AMBIGUOUS }
            if (finalPool.isEmpty()) finalPool = LOWERCASE
        }

        val password = StringBuilder(length)
        for (i in 0 until length) {
            val idx = random.nextInt(finalPool.length)
            password.append(finalPool[idx])
        }
        return password.toString()
    }

    fun calculateStrength(password: String): PasswordStrength {
        if (password.length < 6) return PasswordStrength.VERY_WEAK

        var poolSize = 0
        if (password.any { it.isUpperCase() }) poolSize += 26
        if (password.any { it.isLowerCase() }) poolSize += 26
        if (password.any { it.isDigit() }) poolSize += 10
        if (password.any { !it.isLetterOrDigit() }) poolSize += 30

        if (poolSize == 0) poolSize = 26

        // Entropy = L * log2(poolSize)
        val entropy = password.length * (kotlin.math.log2(poolSize.toDouble()))

        return when {
            entropy < 30 -> PasswordStrength.VERY_WEAK
            entropy < 50 -> PasswordStrength.WEAK
            entropy < 70 -> PasswordStrength.FAIR
            entropy < 90 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
    }

    fun generateUuids(count: Int = 1, uppercase: Boolean = false, includeHyphens: Boolean = true): List<String> {
        val safeCount = count.coerceIn(1, 50)
        return (1..safeCount).map {
            var uuid = UUID.randomUUID().toString()
            if (!includeHyphens) {
                uuid = uuid.replace("-", "")
            }
            if (uppercase) uuid.uppercase() else uuid.lowercase()
        }
    }
}
