package com.nexuskit.app.feature.tools.line_sorter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LineSorterEngineTest {

    @Test
    fun testAlphabeticalSort() {
        val input = "Banana\nApple\nCherry"
        val sorted = LineSorterEngine.sortLines(input, LineSorterEngine.SortType.ALPHABETICAL_ASC)
        assertEquals("Apple\nBanana\nCherry", sorted)
    }

    @Test
    fun testNaturalSort() {
        val input = "item10\nitem2\nitem1"
        val sorted = LineSorterEngine.sortLines(input, LineSorterEngine.SortType.NATURAL_ASC)
        assertEquals("item1\nitem2\nitem10", sorted)
    }

    @Test
    fun testDeduplicate() {
        val input = "apple\nbanana\napple\norange\nbanana"
        val deduped = LineSorterEngine.deduplicate(input, LineSorterEngine.DedupeType.REMOVE_DUPLICATES_CASE_SENSITIVE)
        assertEquals("apple\nbanana\norange", deduped)
    }

    @Test
    fun testWordFrequency() {
        val text = "the quick brown fox jumps over the lazy dog the"
        val freqs = LineSorterEngine.analyzeWordFrequency(text)
        assertEquals("the", freqs[0].word)
        assertEquals(3, freqs[0].count)
    }
}
