package com.nexuskit.app.feature.tools.text_suite

enum class TextTab {
    WORD_COUNTER, CLEANER_SORTER, REVERSER_PALINDROME, REPEATER_TRUNCATOR, LOREM_IPSUM, EXTRACTOR
}

data class TextStatistics(
    val words: Int,
    val charactersWithSpaces: Int,
    val charactersNoSpaces: Int,
    val sentences: Int,
    val paragraphs: Int,
    val readingTimeSeconds: Int
)

object TextSuiteEngine {

    fun analyzeText(text: String): TextStatistics {
        if (text.isEmpty()) {
            return TextStatistics(0, 0, 0, 0, 0, 0)
        }
        val words = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
        val charsWith = text.length
        val charsNo = text.replace(Regex("\\s"), "").length
        val sentences = text.split(Regex("[.!?]+\\s+")).filter { it.isNotBlank() }.size
        val paragraphs = text.split(Regex("\n\n+")).filter { it.isNotBlank() }.size.coerceAtLeast(1)
        val readingSeconds = ((words / 200.0) * 60).toInt().coerceAtLeast(1)
        return TextStatistics(words, charsWith, charsNo, sentences, paragraphs, readingSeconds)
    }

    fun cleanWhitespace(text: String): String {
        return text.lines()
            .map { it.trim().replace(Regex("\\s+"), " ") }
            .filter { it.isNotEmpty() }
            .joinToString("\n")
    }

    fun reverseText(text: String, reverseWordsOnly: Boolean = false): String {
        return if (reverseWordsOnly) {
            text.split(" ").joinToString(" ") { it.reversed() }
        } else {
            text.reversed()
        }
    }

    fun checkPalindrome(text: String): Pair<Boolean, String> {
        val clean = text.lowercase().replace(Regex("[^a-z0-9]"), "")
        if (clean.length < 2) return false to "Enter at least 2 alphanumeric characters"
        val isPal = clean == clean.reversed()
        return isPal to if (isPal) "Yes! \"$text\" is a palindrome." else "No, this text is not a palindrome."
    }

    fun removeDuplicateLines(text: String, ignoreCase: Boolean = true): String {
        val seen = mutableSetOf<String>()
        val result = mutableListOf<String>()
        for (line in text.lines()) {
            val key = if (ignoreCase) line.lowercase().trim() else line.trim()
            if (key.isNotEmpty() && seen.add(key)) {
                result.add(line.trim())
            }
        }
        return result.joinToString("\n")
    }

    fun sortLines(text: String, ascending: Boolean = true, numeric: Boolean = false): String {
        val lines = text.lines().filter { it.isNotBlank() }
        val sorted = if (numeric) {
            lines.sortedWith(compareBy { it.filter { c -> c.isDigit() }.toLongOrNull() ?: 0L })
        } else {
            lines.sorted()
        }
        return if (ascending) sorted.joinToString("\n") else sorted.reversed().joinToString("\n")
    }

    fun repeatText(text: String, count: Int, separator: String = "\n"): String {
        val c = count.coerceIn(1, 500)
        return List(c) { text }.joinToString(separator)
    }

    fun truncateText(text: String, maxChars: Int, suffix: String = "..."): String {
        if (text.length <= maxChars) return text
        return text.take(maxChars) + suffix
    }

    fun extractEmails(text: String): List<String> {
        val regex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return regex.findAll(text).map { it.value }.distinct().toList()
    }

    fun extractUrls(text: String): List<String> {
        val regex = Regex("https?://[a-zA-Z0-9.-]+(?:/[^\\s]*)?")
        return regex.findAll(text).map { it.value }.distinct().toList()
    }

    fun extractPhones(text: String): List<String> {
        val regex = Regex("(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}")
        return regex.findAll(text).map { it.value }.distinct().toList()
    }

    fun generateLoremIpsum(paragraphs: Int = 3): String {
        val sample = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
        return List(paragraphs.coerceIn(1, 10)) { sample }.joinToString("\n\n")
    }
}
