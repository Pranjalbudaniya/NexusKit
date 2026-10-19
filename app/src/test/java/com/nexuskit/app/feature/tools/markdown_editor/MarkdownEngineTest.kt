package com.nexuskit.app.feature.tools.markdown_editor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkdownEngineTest {

    @Test
    fun testEmptyMarkdownStats() {
        val stats = MarkdownEngine.calculateStats("")
        assertEquals(0, stats.words)
        assertEquals(0, stats.characters)
        assertEquals(0, stats.lines)
        assertEquals(0, stats.headingsCount)
    }

    @Test
    fun testMarkdownStatsCalculation() {
        val text = """
            # Sample Title
            
            This is a test paragraph with seven words.
            
            ## Subtitle Here
            - Item 1
            - Item 2
        """.trimIndent()

        val stats = MarkdownEngine.calculateStats(text)
        assertTrue(stats.words > 0)
        assertEquals(2, stats.headingsCount)
        assertTrue(stats.lines >= 7)
    }

    @Test
    fun testHeadingExtractionAndIgnoringCodeBlocks() {
        val md = """
            # Heading 1
            
            Some text
            
            ```
            # This is inside a code block and should not be a heading
            ## Also inside code block
            ```
            
            ## Heading 2
            ### Heading 3
            #### Heading 4
            ##### Heading 5
            ###### Heading 6
        """.trimIndent()

        val headings = MarkdownEngine.extractHeadings(md)
        assertEquals(6, headings.size)
        assertEquals("Heading 1", headings[0].title)
        assertEquals(1, headings[0].level)
        assertEquals("Heading 2", headings[1].title)
        assertEquals(2, headings[1].level)
        assertEquals("Heading 3", headings[2].title)
        assertEquals(3, headings[2].level)
        assertEquals("Heading 6", headings[5].title)
        assertEquals(6, headings[5].level)
    }

    @Test
    fun testBuildHeadingTreeNesting() {
        val md = """
            # Main Topic
            ## Subtopic A
            ### Deep Detail A1
            ## Subtopic B
            # Second Topic
        """.trimIndent()

        val flat = MarkdownEngine.extractHeadings(md)
        val outline = MarkdownEngine.buildHeadingTree(flat)

        assertEquals(5, outline.totalCount)
        assertEquals(2, outline.h1Count)
        assertEquals(2, outline.h2Count)
        assertEquals(1, outline.h3PlusCount)

        // Roots should only have the two H1s
        assertEquals(2, outline.roots.size)
        val root1 = outline.roots[0]
        assertEquals("Main Topic", root1.title)
        assertEquals(2, root1.children.size)

        val subA = root1.children[0]
        assertEquals("Subtopic A", subA.title)
        assertEquals(1, subA.children.size)
        assertEquals("Deep Detail A1", subA.children[0].title)

        val subB = root1.children[1]
        assertEquals("Subtopic B", subB.title)
        assertEquals(0, subB.children.size)

        val root2 = outline.roots[1]
        assertEquals("Second Topic", root2.title)
        assertEquals(0, root2.children.size)
    }

    @Test
    fun testChecklistToggling() {
        val md = """
            # Tasks
            - [ ] Buy milk
            - [x] Read book
        """.trimIndent()

        // Toggle first task to checked
        val toggled1 = MarkdownEngine.toggleChecklistAtLine(md, 2)
        assertTrue(toggled1.contains("- [x] Buy milk"))

        // Toggle second task back to unchecked
        val toggled2 = MarkdownEngine.toggleChecklistAtLine(toggled1, 3)
        assertTrue(toggled2.contains("- [ ] Read book"))
    }

    @Test
    fun testTableTemplateGeneration() {
        val table = MarkdownEngine.generateTableTemplate(rows = 2, cols = 2, hasHeader = true)
        assertTrue(table.contains("| Header 1 | Header 2 |"))
        assertTrue(table.contains("| --- | --- |"))
        assertTrue(table.contains("| Item 1-1 | Item 1-2 |"))
        assertTrue(table.contains("| Item 2-1 | Item 2-2 |"))
    }

    @Test
    fun testSearchAndReplace() {
        val text = "Kotlin is great. I love Kotlin programming."
        val matches = MarkdownEngine.findMatches(text, "Kotlin")
        assertEquals(2, matches.size)
        assertEquals(0, matches[0].startIndex)
        assertEquals(6, matches[0].endIndex)

        val replacedSingle = MarkdownEngine.replaceMatch(text, matches[0], "Jetpack")
        assertEquals("Jetpack is great. I love Kotlin programming.", replacedSingle)

        val replacedAll = MarkdownEngine.replaceAll(text, "Kotlin", "Compose")
        assertEquals("Compose is great. I love Compose programming.", replacedAll)
    }
}
