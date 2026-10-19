package com.nexuskit.app.feature.tools.line_sorter

import java.util.regex.Pattern

data class WordFrequency(
    val word: String,
    val count: Int,
    val percentage: Float
)

data class LineTransformStats(
    val initialLines: Int,
    val finalLines: Int,
    val duplicatesRemoved: Int,
    val totalWords: Int,
    val uniqueWords: Int
)

object LineSorterEngine {

    enum class SortType {
        ALPHABETICAL_ASC,
        ALPHABETICAL_DESC,
        NATURAL_ASC,
        NATURAL_DESC,
        LENGTH_ASC,
        LENGTH_DESC,
        REVERSE,
        SHUFFLE
    }

    enum class DedupeType {
        REMOVE_DUPLICATES_CASE_SENSITIVE,
        REMOVE_DUPLICATES_CASE_INSENSITIVE,
        KEEP_UNIQUE_ONLY,
        KEEP_DUPLICATES_ONLY
    }

    fun sortLines(text: String, sortType: SortType): String {
        val lines = text.lines()
        val sorted = when (sortType) {
            SortType.ALPHABETICAL_ASC -> lines.sorted()
            SortType.ALPHABETICAL_DESC -> lines.sortedDescending()
            SortType.NATURAL_ASC -> lines.sortedWith { a, b -> naturalCompare(a, b) }
            SortType.NATURAL_DESC -> lines.sortedWith { a, b -> naturalCompare(b, a) }
            SortType.LENGTH_ASC -> lines.sortedBy { it.length }
            SortType.LENGTH_DESC -> lines.sortedByDescending { it.length }
            SortType.REVERSE -> lines.reversed()
            SortType.SHUFFLE -> lines.shuffled()
        }
        return sorted.joinToString("\n")
    }

    fun deduplicate(text: String, dedupeType: DedupeType): String {
        val lines = text.lines()
        val result = when (dedupeType) {
            DedupeType.REMOVE_DUPLICATES_CASE_SENSITIVE -> {
                val seen = LinkedHashSet<String>()
                lines.filter { seen.add(it) }
            }
            DedupeType.REMOVE_DUPLICATES_CASE_INSENSITIVE -> {
                val seenLower = mutableSetOf<String>()
                val res = mutableListOf<String>()
                for (line in lines) {
                    if (seenLower.add(line.lowercase())) {
                        res.add(line)
                    }
                }
                res
            }
            DedupeType.KEEP_UNIQUE_ONLY -> {
                val counts = lines.groupingBy { it }.eachCount()
                lines.filter { counts[it] == 1 }
            }
            DedupeType.KEEP_DUPLICATES_ONLY -> {
                val counts = lines.groupingBy { it }.eachCount()
                val seen = mutableSetOf<String>()
                lines.filter { (counts[it] ?: 0) > 1 && seen.add(it) }
            }
        }
        return result.joinToString("\n")
    }

    fun cleanLines(
        text: String,
        trimWhitespace: Boolean,
        removeEmpty: Boolean,
        addLineNumbers: Boolean,
        prefix: String = "",
        suffix: String = ""
    ): String {
        var lines = text.lines()
        if (trimWhitespace) {
            lines = lines.map { it.trim() }
        }
        if (removeEmpty) {
            lines = lines.filter { it.isNotBlank() }
        }
        if (prefix.isNotEmpty() || suffix.isNotEmpty()) {
            lines = lines.map { "$prefix$it$suffix" }
        }
        if (addLineNumbers) {
            val padWidth = lines.size.toString().length
            lines = lines.mapIndexed { idx, line ->
                val num = (idx + 1).toString().padStart(padWidth, ' ')
                "$num. $line"
            }
        }
        return lines.joinToString("\n")
    }

    fun analyzeWordFrequency(text: String): List<WordFrequency> {
        if (text.isBlank()) return emptyList()

        val tokens = text.lowercase()
            .split("[^\\p{L}\\p{Nd}_]+".toRegex())
            .filter { it.isNotBlank() }

        if (tokens.isEmpty()) return emptyList()

        val totalWords = tokens.size.toFloat()
        val frequencyMap = tokens.groupingBy { it }.eachCount()

        return frequencyMap.entries
            .sortedByDescending { it.value }
            .map { (word, count) ->
                WordFrequency(
                    word = word,
                    count = count,
                    percentage = (count / totalWords) * 100f
                )
            }
    }

    private fun naturalCompare(a: String, b: String): Int {
        val pattern = Pattern.compile("(\\d+|\\D+)")
        val matcherA = pattern.matcher(a)
        val matcherB = pattern.matcher(b)

        while (matcherA.find() && matcherB.find()) {
            val tokenA = matcherA.group()
            val tokenB = matcherB.group()

            val numA = tokenA.toLongOrNull()
            val numB = tokenB.toLongOrNull()

            if (numA != null && numB != null) {
                val cmp = numA.compareTo(numB)
                if (cmp != 0) return cmp
            } else {
                val cmp = tokenA.compareTo(tokenB, ignoreCase = true)
                if (cmp != 0) return cmp
            }
        }

        return a.length.compareTo(b.length)
    }
}
