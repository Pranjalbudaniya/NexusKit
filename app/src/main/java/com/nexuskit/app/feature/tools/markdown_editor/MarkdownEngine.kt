package com.nexuskit.app.feature.tools.markdown_editor

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

data class DocumentStats(
    val words: Int = 0,
    val characters: Int = 0,
    val charactersNoSpaces: Int = 0,
    val lines: Int = 0,
    val paragraphs: Int = 0,
    val headingsCount: Int = 0,
    val readingTimeMinutes: Int = 0,
    val speakingTimeMinutes: Int = 0
)

data class HeadingNode(
    val id: String,
    val title: String,
    val level: Int, // 1 to 6
    val lineNumber: Int, // 1-indexed
    val charOffset: Int,
    val children: MutableList<HeadingNode> = mutableListOf()
)

data class MarkdownOutline(
    val roots: List<HeadingNode> = emptyList(),
    val totalCount: Int = 0,
    val h1Count: Int = 0,
    val h2Count: Int = 0,
    val h3PlusCount: Int = 0
)

data class SearchMatch(
    val startIndex: Int,
    val endIndex: Int,
    val lineNumber: Int,
    val snippet: String
)

data class MarkdownTable(
    val headers: List<String>,
    val rows: List<List<String>>
)

object MarkdownEngine {

    fun calculateStats(text: String): DocumentStats {
        if (text.isBlank()) {
            return DocumentStats()
        }
        val words = text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        val chars = text.length
        val charsNoSpaces = text.count { !it.isWhitespace() }
        val lines = text.lines().size
        val paragraphs = text.split("\n\n+").filter { it.isNotBlank() }.size
        val headings = extractHeadings(text).size
        val readingTime = (words / 200).coerceAtLeast(1)
        val speakingTime = (words / 130).coerceAtLeast(1)

        return DocumentStats(
            words = words,
            characters = chars,
            charactersNoSpaces = charsNoSpaces,
            lines = lines,
            paragraphs = paragraphs,
            headingsCount = headings,
            readingTimeMinutes = readingTime,
            speakingTimeMinutes = speakingTime
        )
    }

    fun extractHeadings(markdown: String): List<HeadingNode> {
        if (markdown.isBlank()) return emptyList()

        val headings = mutableListOf<HeadingNode>()
        val lines = markdown.lines()
        var inCodeBlock = false
        var currentOffset = 0

        val headingRegex = Regex("""^(#{1,6})\s+(.+)$""")

        for (index in lines.indices) {
            val line = lines[index]
            val trimmed = line.trim()

            if (trimmed.startsWith("```")) {
                inCodeBlock = !inCodeBlock
                currentOffset += line.length + 1
                continue
            }

            if (!inCodeBlock) {
                val match = headingRegex.find(trimmed)
                if (match != null) {
                    val hashes = match.groupValues[1]
                    val title = match.groupValues[2].trim()
                    val level = hashes.length.coerceIn(1, 6)
                    headings.add(
                        HeadingNode(
                            id = "heading_${index}_${headings.size}",
                            title = title,
                            level = level,
                            lineNumber = index + 1,
                            charOffset = currentOffset
                        )
                    )
                }
            }

            currentOffset += line.length + 1
        }

        return headings
    }

    fun buildHeadingTree(flatHeadings: List<HeadingNode>): MarkdownOutline {
        if (flatHeadings.isEmpty()) {
            return MarkdownOutline()
        }

        val roots = mutableListOf<HeadingNode>()
        val stack = ArrayDeque<HeadingNode>()

        var h1 = 0
        var h2 = 0
        var h3Plus = 0

        for (heading in flatHeadings) {
            when (heading.level) {
                1 -> h1++
                2 -> h2++
                else -> h3Plus++
            }

            val node = heading.copy(children = mutableListOf())

            while (stack.isNotEmpty() && stack.last().level >= node.level) {
                stack.removeLast()
            }

            if (stack.isEmpty()) {
                roots.add(node)
            } else {
                stack.last().children.add(node)
            }

            stack.addLast(node)
        }

        return MarkdownOutline(
            roots = roots,
            totalCount = flatHeadings.size,
            h1Count = h1,
            h2Count = h2,
            h3PlusCount = h3Plus
        )
    }

    fun toggleChecklistAtLine(markdown: String, targetLineNumber: Int): String {
        val lines = markdown.lines().toMutableList()
        val targetIndex = targetLineNumber - 1
        if (targetIndex !in lines.indices) return markdown

        val line = lines[targetIndex]
        val trimmed = line.trimStart()
        val indent = line.substring(0, line.length - trimmed.length)

        val updatedLine = when {
            trimmed.startsWith("- [ ] ") -> indent + "- [x] " + trimmed.removePrefix("- [ ] ")
            trimmed.startsWith("- [x] ") -> indent + "- [ ] " + trimmed.removePrefix("- [x] ")
            trimmed.startsWith("- [X] ") -> indent + "- [ ] " + trimmed.removePrefix("- [X] ")
            trimmed.startsWith("* [ ] ") -> indent + "* [x] " + trimmed.removePrefix("* [ ] ")
            trimmed.startsWith("* [x] ") -> indent + "* [ ] " + trimmed.removePrefix("* [x] ")
            trimmed.startsWith("* [X] ") -> indent + "* [ ] " + trimmed.removePrefix("* [X] ")
            else -> line
        }

        lines[targetIndex] = updatedLine
        return lines.joinToString("\n")
    }

    fun generateTableTemplate(rows: Int, cols: Int, hasHeader: Boolean = true): String {
        val r = rows.coerceIn(1, 20)
        val c = cols.coerceIn(1, 10)
        val sb = StringBuilder()

        if (hasHeader) {
            sb.append("|")
            for (j in 1..c) sb.append(" Header $j |")
            sb.append("\n|")
            for (j in 1..c) sb.append(" --- |")
            sb.append("\n")
        }

        for (i in 1..r) {
            sb.append("|")
            for (j in 1..c) sb.append(" Item $i-$j |")
            sb.append("\n")
        }

        return sb.toString()
    }

    fun findMatches(text: String, query: String, matchCase: Boolean = false): List<SearchMatch> {
        if (query.isEmpty() || text.isEmpty()) return emptyList()

        val matches = mutableListOf<SearchMatch>()
        val lines = text.lines()
        var currentOffset = 0

        for (lineIndex in lines.indices) {
            val line = lines[lineIndex]
            var searchFrom = 0

            while (searchFrom < line.length) {
                val foundIndex = line.indexOf(query, searchFrom, ignoreCase = !matchCase)
                if (foundIndex == -1) break

                val globalStart = currentOffset + foundIndex
                val globalEnd = globalStart + query.length
                val snippet = line.trim()

                matches.add(
                    SearchMatch(
                        startIndex = globalStart,
                        endIndex = globalEnd,
                        lineNumber = lineIndex + 1,
                        snippet = snippet
                    )
                )

                searchFrom = foundIndex + query.length.coerceAtLeast(1)
            }

            currentOffset += line.length + 1
        }

        return matches
    }

    fun replaceMatch(text: String, match: SearchMatch, replacement: String): String {
        if (match.startIndex !in text.indices || match.endIndex > text.length) return text
        return text.substring(0, match.startIndex) + replacement + text.substring(match.endIndex)
    }

    fun replaceAll(text: String, query: String, replacement: String, matchCase: Boolean = false): String {
        if (query.isEmpty()) return text
        return if (matchCase) {
            text.replace(query, replacement)
        } else {
            text.replace(Regex(Regex.escape(query), RegexOption.IGNORE_CASE), replacement)
        }
    }

    fun parseTableFromLines(lines: List<String>): MarkdownTable? {
        if (lines.size < 2) return null
        val headerLine = lines[0].trim()
        val separatorLine = lines[1].trim()

        if (!headerLine.startsWith("|") || !headerLine.endsWith("|") ||
            !separatorLine.startsWith("|") || !separatorLine.endsWith("|")
        ) {
            return null
        }

        val headers = headerLine.trim('|').split('|').map { it.trim() }
        val rows = mutableListOf<List<String>>()

        for (i in 2 until lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("|") && line.endsWith("|")) {
                val cells = line.trim('|').split('|').map { it.trim() }
                rows.add(cells)
            }
        }

        return MarkdownTable(headers, rows)
    }

    fun markdownToHtml(markdown: String, title: String = "NexusKit Document"): String {
        val bodyHtml = StringBuilder()
        val lines = markdown.lines()
        var inCodeBlock = false
        var inList = false

        for (line in lines) {
            val trimmed = line.trim()

            // Code blocks
            if (trimmed.startsWith("```")) {
                if (inCodeBlock) {
                    bodyHtml.append("</code></pre>\n")
                    inCodeBlock = false
                } else {
                    bodyHtml.append("<pre><code>")
                    inCodeBlock = true
                }
                continue
            }
            if (inCodeBlock) {
                bodyHtml.append(escapeHtml(line)).append("\n")
                continue
            }

            // Task list checkboxes
            if (trimmed.startsWith("- [ ] ") || trimmed.startsWith("* [ ] ")) {
                val content = trimmed.substring(6)
                bodyHtml.append("<div class=\"task-item\"><input type=\"checkbox\" disabled> <span>")
                    .append(formatInline(content))
                    .append("</span></div>\n")
                continue
            } else if (trimmed.startsWith("- [x] ") || trimmed.startsWith("- [X] ") ||
                trimmed.startsWith("* [x] ") || trimmed.startsWith("* [X] ")
            ) {
                val content = trimmed.substring(6)
                bodyHtml.append("<div class=\"task-item\"><input type=\"checkbox\" checked disabled> <span class=\"done\">")
                    .append(formatInline(content))
                    .append("</span></div>\n")
                continue
            }

            // Unordered list
            if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                if (!inList) {
                    bodyHtml.append("<ul>\n")
                    inList = true
                }
                val content = trimmed.substring(2)
                bodyHtml.append("  <li>").append(formatInline(content)).append("</li>\n")
                continue
            } else if (inList) {
                bodyHtml.append("</ul>\n")
                inList = false
            }

            // Headers
            if (trimmed.startsWith("### ")) {
                bodyHtml.append("<h3>").append(formatInline(trimmed.removePrefix("### "))).append("</h3>\n")
            } else if (trimmed.startsWith("## ")) {
                bodyHtml.append("<h2>").append(formatInline(trimmed.removePrefix("## "))).append("</h2>\n")
            } else if (trimmed.startsWith("# ")) {
                bodyHtml.append("<h1>").append(formatInline(trimmed.removePrefix("# "))).append("</h1>\n")
            } else if (trimmed.startsWith("> ")) {
                bodyHtml.append("<blockquote>").append(formatInline(trimmed.removePrefix("> "))).append("</blockquote>\n")
            } else if (trimmed == "---" || trimmed == "***") {
                bodyHtml.append("<hr/>\n")
            } else if (trimmed.isEmpty()) {
                bodyHtml.append("<br/>\n")
            } else {
                bodyHtml.append("<p>").append(formatInline(trimmed)).append("</p>\n")
            }
        }

        if (inCodeBlock) bodyHtml.append("</code></pre>\n")
        if (inList) bodyHtml.append("</ul>\n")

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>${escapeHtml(title)}</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                        line-height: 1.6;
                        color: #1a1a1a;
                        max-width: 800px;
                        margin: 24px auto;
                        padding: 0 16px;
                    }
                    h1, h2, h3 { color: #0066cc; border-bottom: 1px solid #e1e4e8; padding-bottom: 6px; }
                    pre {
                        background-color: #f6f8fa;
                        border-radius: 8px;
                        padding: 12px;
                        overflow-x: auto;
                        font-family: monospace;
                    }
                    blockquote {
                        border-left: 4px solid #0066cc;
                        margin: 0;
                        padding-left: 16px;
                        color: #555;
                        font-style: italic;
                    }
                    code { background: #f0f2f5; padding: 2px 6px; border-radius: 4px; font-family: monospace; }
                    hr { border: 0; height: 1px; background: #e1e4e8; margin: 20px 0; }
                    .task-item { display: flex; align-items: center; gap: 8px; margin: 4px 0; }
                    .task-item .done { text-decoration: line-through; opacity: 0.6; }
                    table { border-collapse: collapse; width: 100%; margin: 16px 0; }
                    th, td { border: 1px solid #dfe2e5; padding: 8px 12px; text-align: left; }
                    th { background-color: #f6f8fa; font-weight: 600; }
                    @media print {
                        body { max-width: 100%; margin: 0; }
                    }
                </style>
            </head>
            <body>
            $bodyHtml
            </body>
            </html>
        """.trimIndent()
    }

    fun exportToPdf(context: Context, markdown: String, fileName: String = "document"): Result<File> {
        return runCatching {
            val doc = PdfDocument()
            val pageWidth = 595 // A4 standard width in points (72 dpi)
            val pageHeight = 842 // A4 standard height
            val margin = 48f
            val contentWidth = pageWidth - (margin * 2)

            val paint = Paint().apply {
                isAntiAlias = true
                color = Color.BLACK
                textSize = 12f
            }
            val titlePaint = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(0, 102, 204)
                textSize = 20f
                isFakeBoldText = true
            }
            val h2Paint = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(30, 41, 59)
                textSize = 15f
                isFakeBoldText = true
            }

            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = doc.startPage(pageInfo)
            var canvas = page.canvas
            var yPos = margin + 20f

            val lines = markdown.lines()
            for (line in lines) {
                val trimmed = line.trim()

                // Check if page overflow
                if (yPos > pageHeight - margin - 40f) {
                    doc.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = doc.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = margin + 20f
                }

                when {
                    trimmed.startsWith("# ") -> {
                        yPos += 10f
                        canvas.drawText(trimmed.removePrefix("# "), margin, yPos, titlePaint)
                        yPos += 26f
                    }
                    trimmed.startsWith("## ") -> {
                        yPos += 8f
                        canvas.drawText(trimmed.removePrefix("## "), margin, yPos, h2Paint)
                        yPos += 20f
                    }
                    trimmed.startsWith("### ") -> {
                        yPos += 6f
                        canvas.drawText(trimmed.removePrefix("### "), margin, yPos, h2Paint)
                        yPos += 18f
                    }
                    trimmed.startsWith("- [x] ") || trimmed.startsWith("- [X] ") -> {
                        canvas.drawText("☑  ${trimmed.substring(6)}", margin + 10f, yPos, paint)
                        yPos += 16f
                    }
                    trimmed.startsWith("- [ ] ") -> {
                        canvas.drawText("☐  ${trimmed.substring(6)}", margin + 10f, yPos, paint)
                        yPos += 16f
                    }
                    trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                        canvas.drawText("•  ${trimmed.substring(2)}", margin + 10f, yPos, paint)
                        yPos += 16f
                    }
                    trimmed == "---" || trimmed == "***" -> {
                        canvas.drawLine(margin, yPos, margin + contentWidth, yPos, paint)
                        yPos += 16f
                    }
                    trimmed.isEmpty() -> {
                        yPos += 10f
                    }
                    else -> {
                        val words = trimmed.split(" ")
                        var lineBuf = ""
                        for (w in words) {
                            val testLine = if (lineBuf.isEmpty()) w else "$lineBuf $w"
                            if (paint.measureText(testLine) > contentWidth) {
                                canvas.drawText(lineBuf, margin, yPos, paint)
                                yPos += 15f
                                lineBuf = w
                                if (yPos > pageHeight - margin - 40f) {
                                    doc.finishPage(page)
                                    pageNumber++
                                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                                    page = doc.startPage(pageInfo)
                                    canvas = page.canvas
                                    yPos = margin + 20f
                                }
                            } else {
                                lineBuf = testLine
                            }
                        }
                        if (lineBuf.isNotEmpty()) {
                            canvas.drawText(lineBuf, margin, yPos, paint)
                            yPos += 16f
                        }
                    }
                }
            }

            doc.finishPage(page)

            val exportDir = File(context.cacheDir, "markdown_exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val pdfFile = File(exportDir, "${fileName}_${System.currentTimeMillis()}.pdf")
            FileOutputStream(pdfFile).use { out ->
                doc.writeTo(out)
            }
            doc.close()

            pdfFile
        }
    }

    fun exportToHtmlFile(context: Context, markdown: String, fileName: String = "document"): Result<File> {
        return runCatching {
            val html = markdownToHtml(markdown, fileName)
            val exportDir = File(context.cacheDir, "markdown_exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val htmlFile = File(exportDir, "${fileName}_${System.currentTimeMillis()}.html")
            htmlFile.writeText(html, Charsets.UTF_8)
            htmlFile
        }
    }

    private fun formatInline(text: String): String {
        var res = escapeHtml(text)
        // Bold
        res = res.replace("\\*\\*(.*?)\\*\\*".toRegex(), "<strong>$1</strong>")
        res = res.replace("__(.*?)__".toRegex(), "<strong>$1</strong>")
        // Strikethrough
        res = res.replace("~~(.*?)~~".toRegex(), "<del>$1</del>")
        // Italic
        res = res.replace("\\*(.*?)\\*".toRegex(), "<em>$1</em>")
        res = res.replace("_(.*?)_".toRegex(), "<em>$1</em>")
        // Inline code
        res = res.replace("`(.*?)`".toRegex(), "<code>$1</code>")
        // Links
        res = res.replace("\\[(.*?)\\]\\((.*?)\\)".toRegex(), "<a href=\"$2\">$1</a>")
        return res
    }

    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}
