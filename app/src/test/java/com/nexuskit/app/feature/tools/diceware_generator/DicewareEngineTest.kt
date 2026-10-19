package com.nexuskit.app.feature.tools.diceware_generator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DicewareEngineTest {

    @Test
    fun testGeneratePassphraseWordCount() {
        val pass = DicewareEngine.generatePassphrase(wordCount = 5, delimiter = "-")
        assertEquals(5, pass.wordCount)
        val parts = pass.passphrase.split("-")
        assertEquals(5, parts.size)
        assertTrue(pass.entropyBits > 50.0)
    }

    @Test
    fun testPasswordStrengthEvaluation() {
        val weak = DicewareEngine.evaluatePasswordStrength("123456")
        assertTrue(weak.score <= 1)

        val strong = DicewareEngine.evaluatePasswordStrength("Correct-Horse-Battery-Staple-99!")
        assertTrue(strong.score >= 3)
        assertTrue(strong.isLongEnough)
        assertTrue(strong.hasUppercase)
        assertTrue(strong.hasLowercase)
        assertTrue(strong.hasNumbers)
        assertTrue(strong.hasSymbols)
    }
}
