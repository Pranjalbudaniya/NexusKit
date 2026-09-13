package com.nexuskit.app.feature.tools.base_converter

import java.math.BigInteger

enum class NumberBase(val radix: Int, val displayName: String, val shortName: String, val prefix: String) {
    HEX(16, "Hexadecimal", "HEX", "0x"),
    DEC(10, "Decimal", "DEC", ""),
    OCT(8, "Octal", "OCT", "0o"),
    BIN(2, "Binary", "BIN", "0b")
}

data class BaseConversionResult(
    val hex: String,
    val dec: String,
    val oct: String,
    val bin: String,
    val bitLength: Int,
    val asciiRepresentation: String
)

object BaseConverterEngine {

    fun convertFrom(input: String, base: NumberBase): BaseConversionResult? {
        val clean = input.trim().replace(" ", "").replace("_", "")
        if (clean.isEmpty()) return null

        return try {
            val bigInt = BigInteger(clean, base.radix)
            val hex = bigInt.toString(16).uppercase()
            val dec = bigInt.toString(10)
            val oct = bigInt.toString(8)
            val bin = bigInt.toString(2)

            // Format binary with spaces every 4 bits
            val formattedBin = formatBinary(bin)

            // ASCII interpretation if within valid char range
            val ascii = if (bigInt >= BigInteger.ZERO && bigInt <= BigInteger.valueOf(127)) {
                val code = bigInt.toInt()
                if (code in 32..126) "'${code.toChar()}'" else "Non-printable ($code)"
            } else {
                "Out of ASCII range"
            }

            BaseConversionResult(
                hex = hex,
                dec = dec,
                oct = oct,
                bin = formattedBin,
                bitLength = bigInt.bitLength(),
                asciiRepresentation = ascii
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun formatBinary(bin: String): String {
        val padded = if (bin.length % 4 != 0) {
            "0".repeat(4 - (bin.length % 4)) + bin
        } else {
            bin
        }
        return padded.chunked(4).joinToString(" ")
    }

    fun isValidCharForBase(char: Char, base: NumberBase): Boolean {
        val upper = char.uppercaseChar()
        return when (base) {
            NumberBase.BIN -> upper in "01"
            NumberBase.OCT -> upper in "01234567"
            NumberBase.DEC -> upper in "0123456789"
            NumberBase.HEX -> upper in "0123456789ABCDEF"
        }
    }
}
