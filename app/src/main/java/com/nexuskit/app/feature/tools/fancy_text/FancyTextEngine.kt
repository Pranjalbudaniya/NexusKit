package com.nexuskit.app.feature.tools.fancy_text

data class FancyTextStyle(
    val id: String,
    val name: String,
    val category: String,
    val previewSample: String
)

data class FancyTextItem(
    val style: FancyTextStyle,
    val convertedText: String
)

object FancyTextEngine {

    val STYLES = listOf(
        FancyTextStyle("bold_sans", "Bold (Sans)", "Bold", "𝗡𝗲𝘅𝘂𝘀𝗞𝗶𝘁"),
        FancyTextStyle("bold_serif", "Bold (Serif)", "Bold", "𝐍𝐞𝐱𝐮𝐬𝐊𝐢𝐭"),
        FancyTextStyle("italic_sans", "Italic (Sans)", "Italic", "𝘕𝘦𝘹𝘶𝘴𝘒𝘪𝘵"),
        FancyTextStyle("italic_serif", "Italic (Serif)", "Italic", "𝑁𝑒𝑥𝑢𝑠𝐾𝑖𝑡"),
        FancyTextStyle("bold_italic_sans", "Bold Italic (Sans)", "Bold", "𝙉𝙚𝙭𝙪𝙨𝙆𝙞𝙩"),
        FancyTextStyle("bold_italic_serif", "Bold Italic (Serif)", "Bold", "𝑵𝒆𝒙𝒖𝒔𝑲𝒊𝒕"),
        FancyTextStyle("monospace", "Monospace", "Retro", "𝙽𝚎𝚡𝚞𝚜𝙺𝚒𝚝"),
        FancyTextStyle("double_struck", "Double-Struck / Blackboard", "Aesthetic", "ℕ𝕖𝕩𝕦𝕤𝕂𝕚𝕥"),
        FancyTextStyle("script", "Cursive / Script", "Calligraphy", "𝒩ℯ𝓍𝓊𝓈𝒦𝒾𝓉"),
        FancyTextStyle("bold_script", "Bold Cursive", "Calligraphy", "𝓝𝓮𝔁𝓾𝓼𝓚𝓲𝓽"),
        FancyTextStyle("fraktur", "Fraktur / Gothic", "Gothic", "𝔑𝔢𝔵𝔲𝔰𝔎𝔦𝔱"),
        FancyTextStyle("bold_fraktur", "Bold Gothic", "Gothic", "𝕹𝖊𝖝𝖚𝖘𝕶𝖎𝖙"),
        FancyTextStyle("circled_white", "Circled", "Symbols", "ⓃⓔⓧⓤⓢⓀⓘⓣ"),
        FancyTextStyle("circled_black", "Circled (Dark)", "Symbols", "🅝🅔🅧🅤🅢🅚🅘🅣"),
        FancyTextStyle("squared_white", "Squared", "Symbols", "🄽🄴🅇🅄🅂🄺🄸🅃"),
        FancyTextStyle("squared_black", "Squared (Dark)", "Symbols", "🅽🅴🆇🆄🆂🅺🅸🆃"),
        FancyTextStyle("small_caps", "Small Caps", "Aesthetic", "NᴇxᴜsKɪᴛ"),
        FancyTextStyle("fullwidth", "Fullwidth (Vaporwave)", "Aesthetic", "ＮｅｘｕｓＫｉｔ"),
        FancyTextStyle("upside_down", "Upside Down", "Fun", "ʇıʞsnxǝN"),
        FancyTextStyle("strikethrough", "Strikethrough", "Decorative", "N̶e̶x̶u̶s̶K̶i̶t̶"),
        FancyTextStyle("underline", "Underline", "Decorative", "N̲e̲x̲u̲s̲K̲i̲t̲"),
        FancyTextStyle("slash", "Slash Overlay", "Decorative", "N̷e̷x̷u̷s̷K̷i̷t̷"),
        FancyTextStyle("wave", "Wavy Accent", "Decorative", "Ñẽx̃ũs̃K̃ĩt̃"),
        FancyTextStyle("zalgo_mild", "Glitch / Zalgo (Mild)", "Glitch", "N̶͝e̶x̶u̶s̶K̶i̶t̶")
    )

    fun convertAll(text: String): List<FancyTextItem> {
        val input = if (text.isEmpty()) "NexusKit" else text
        return STYLES.map { style ->
            FancyTextItem(
                style = style,
                convertedText = applyStyle(input, style.id)
            )
        }
    }

    fun applyStyle(input: String, styleId: String): String {
        return when (styleId) {
            "bold_sans" -> mapOffsets(input, 0x1D5D4, 0x1D5EE, 0x1D7EC)
            "bold_serif" -> mapOffsets(input, 0x1D400, 0x1D41A, 0x1D7CE)
            "italic_sans" -> mapOffsets(input, 0x1D608, 0x1D622, null)
            "italic_serif" -> mapOffsets(input, 0x1D434, 0x1D44E, null)
            "bold_italic_sans" -> mapOffsets(input, 0x1D63C, 0x1D656, null)
            "bold_italic_serif" -> mapOffsets(input, 0x1D468, 0x1D482, null)
            "monospace" -> mapOffsets(input, 0x1D670, 0x1D68A, 0x1D7F6)
            "double_struck" -> mapDoubleStruck(input)
            "script" -> mapScript(input)
            "bold_script" -> mapOffsets(input, 0x1D4D0, 0x1D4EA, null)
            "fraktur" -> mapFraktur(input)
            "bold_fraktur" -> mapOffsets(input, 0x1D56C, 0x1D586, null)
            "circled_white" -> mapCircled(input)
            "circled_black" -> mapCircledBlack(input)
            "squared_white" -> mapSquared(input)
            "squared_black" -> mapSquaredBlack(input)
            "small_caps" -> mapSmallCaps(input)
            "fullwidth" -> mapFullwidth(input)
            "upside_down" -> mapUpsideDown(input)
            "strikethrough" -> input.map { "$it\u0336" }.joinToString("")
            "underline" -> input.map { "$it\u0332" }.joinToString("")
            "slash" -> input.map { "$it\u0337" }.joinToString("")
            "wave" -> input.map { "$it\u0303" }.joinToString("")
            "zalgo_mild" -> mapZalgo(input)
            else -> input
        }
    }

    private fun mapOffsets(input: String, upperBase: Int, lowerBase: Int, digitBase: Int?): String {
        val sb = StringBuilder()
        for (c in input) {
            when {
                c in 'A'..'Z' -> sb.appendCodePoint(upperBase + (c - 'A'))
                c in 'a'..'z' -> sb.appendCodePoint(lowerBase + (c - 'a'))
                digitBase != null && c in '0'..'9' -> sb.appendCodePoint(digitBase + (c - '0'))
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun mapDoubleStruck(input: String): String {
        val sb = StringBuilder()
        for (c in input) {
            when (c) {
                'C' -> sb.append('ℂ')
                'H' -> sb.append('ℍ')
                'N' -> sb.append('ℕ')
                'P' -> sb.append('ℙ')
                'Q' -> sb.append('ℚ')
                'R' -> sb.append('ℝ')
                'Z' -> sb.append('ℤ')
                in 'A'..'Z' -> sb.appendCodePoint(0x1D538 + (c - 'A'))
                in 'a'..'z' -> sb.appendCodePoint(0x1D552 + (c - 'a'))
                in '0'..'9' -> sb.appendCodePoint(0x1D7D8 + (c - '0'))
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun mapScript(input: String): String {
        val sb = StringBuilder()
        for (c in input) {
            when (c) {
                'B' -> sb.append('ℬ')
                'E' -> sb.append('ℰ')
                'F' -> sb.append('ℱ')
                'H' -> sb.append('ℋ')
                'I' -> sb.append('ℐ')
                'L' -> sb.append('ℒ')
                'M' -> sb.append('ℳ')
                'R' -> sb.append('ℛ')
                'e' -> sb.append('ℯ')
                'g' -> sb.append('ℊ')
                'o' -> sb.append('ℴ')
                in 'A'..'Z' -> sb.appendCodePoint(0x1D49C + (c - 'A'))
                in 'a'..'z' -> sb.appendCodePoint(0x1D4B6 + (c - 'a'))
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun mapFraktur(input: String): String {
        val sb = StringBuilder()
        for (c in input) {
            when (c) {
                'C' -> sb.append('ℭ')
                'H' -> sb.append('ℌ')
                'I' -> sb.append('ℑ')
                'R' -> sb.append('ℜ')
                'Z' -> sb.append('ℨ')
                in 'A'..'Z' -> sb.appendCodePoint(0x1D504 + (c - 'A'))
                in 'a'..'z' -> sb.appendCodePoint(0x1D51E + (c - 'a'))
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun mapCircled(input: String): String {
        val sb = StringBuilder()
        for (c in input) {
            when {
                c in 'A'..'Z' -> sb.appendCodePoint(0x24B6 + (c - 'A'))
                c in 'a'..'z' -> sb.appendCodePoint(0x24D0 + (c - 'a'))
                c == '0' -> sb.append('⓪')
                c in '1'..'9' -> sb.appendCodePoint(0x2460 + (c - '1'))
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun mapCircledBlack(input: String): String {
        val sb = StringBuilder()
        for (c in input) {
            when {
                c in 'A'..'Z' -> sb.appendCodePoint(0x1F150 + (c - 'A'))
                c in 'a'..'z' -> sb.appendCodePoint(0x1F150 + (c - 'a'))
                c in '0'..'9' -> sb.appendCodePoint(0x24FF)
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun mapSquared(input: String): String {
        val sb = StringBuilder()
        for (c in input) {
            when {
                c in 'A'..'Z' -> sb.appendCodePoint(0x1F130 + (c - 'A'))
                c in 'a'..'z' -> sb.appendCodePoint(0x1F130 + (c - 'a'))
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun mapSquaredBlack(input: String): String {
        val sb = StringBuilder()
        for (c in input) {
            when {
                c in 'A'..'Z' -> sb.appendCodePoint(0x1F170 + (c - 'A'))
                c in 'a'..'z' -> sb.appendCodePoint(0x1F170 + (c - 'a'))
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun mapSmallCaps(input: String): String {
        val map = mapOf(
            'a' to 'ᴀ', 'b' to 'ʙ', 'c' to 'ᴄ', 'd' to 'ᴅ', 'e' to 'ᴇ', 'f' to 'ꜰ',
            'g' to 'ɢ', 'h' to 'ʜ', 'i' to 'ɪ', 'j' to 'ᴊ', 'k' to 'ᴋ', 'l' to 'ʟ',
            'm' to 'ᴍ', 'n' to 'ɴ', 'o' to 'ᴏ', 'p' to 'ᴘ', 'q' to 'ꞯ', 'r' to 'ʀ',
            's' to 's', 't' to 'ᴛ', 'u' to 'ᴜ', 'v' to 'ᴠ', 'w' to 'ᴡ', 'x' to 'x',
            'y' to 'ʏ', 'z' to 'ᴢ'
        )
        return input.map { c ->
            val lower = c.lowercaseChar()
            map[lower] ?: c
        }.joinToString("")
    }

    private fun mapFullwidth(input: String): String {
        val sb = StringBuilder()
        for (c in input) {
            when {
                c == ' ' -> sb.append('\u3000')
                c.code in 33..126 -> sb.append((c.code + 0xFEE0).toChar())
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun mapUpsideDown(input: String): String {
        val normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789?,!._'\""
        val flipped = "ɐqɔpǝɟƃɥıɾʞןɯuodbɹsʇnʌʍxʎz∀ᗺƆᗡƎℲ⅁HIſʞ˥WNOԀΌᴚS⊥∩ΛMX⅄Z0ƖᄅƐㄣϛ9ㄥ86¿'¡˙‾,„"
        val map = normal.zip(flipped).toMap()
        return input.reversed().map { map[it] ?: it }.joinToString("")
    }

    private fun mapZalgo(input: String): String {
        val marks = listOf('\u0300', '\u0301', '\u0302', '\u0303', '\u0304', '\u0306', '\u0311', '\u0316', '\u0317', '\u032C')
        val sb = StringBuilder()
        for (c in input) {
            sb.append(c)
            if (c.isLetterOrDigit()) {
                val mark = marks[Math.abs(c.hashCode()) % marks.size]
                sb.append(mark)
            }
        }
        return sb.toString()
    }
}
