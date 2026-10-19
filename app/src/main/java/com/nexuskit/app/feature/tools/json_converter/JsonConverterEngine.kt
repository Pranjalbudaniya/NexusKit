package com.nexuskit.app.feature.tools.json_converter

import org.json.JSONArray
import org.json.JSONObject
import java.io.StringReader
import java.util.regex.Pattern

enum class ConvertMode(val title: String) {
    JSON_TO_CSV("JSON → CSV"),
    CSV_TO_JSON("CSV → JSON"),
    JSON_TO_YAML("JSON → YAML"),
    YAML_TO_JSON("YAML → JSON")
}

object JsonConverterEngine {

    // ── JSON TO CSV ─────────────────────────────────────────────────────────

    fun jsonToCsv(jsonString: String, delimiter: Char = ','): Result<String> = runCatching {
        val trimmed = jsonString.trim()
        if (trimmed.isEmpty()) return@runCatching ""

        val jsonArray: JSONArray = when {
            trimmed.startsWith("[") -> JSONArray(trimmed)
            trimmed.startsWith("{") -> JSONArray().put(JSONObject(trimmed))
            else -> throw IllegalArgumentException("JSON must be an object {...} or array of objects [...]")
        }

        if (jsonArray.length() == 0) return@runCatching ""

        // Collect all distinct flattened column keys in order of appearance
        val flattenedRows = mutableListOf<Map<String, String>>()
        val allHeaders = LinkedHashSet<String>()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.optJSONObject(i)
                ?: throw IllegalArgumentException("Element at index $i is not a JSON object")
            val flatMap = mutableMapOf<String, String>()
            flattenObject("", item, flatMap)
            flattenedRows.add(flatMap)
            allHeaders.addAll(flatMap.keys)
        }

        val headerList = allHeaders.toList()
        val sb = StringBuilder()

        // Header row
        sb.append(headerList.joinToString(delimiter.toString()) { escapeCsv(it, delimiter) })
        sb.append("\n")

        // Data rows
        for (row in flattenedRows) {
            val rowValues = headerList.map { key ->
                escapeCsv(row[key] ?: "", delimiter)
            }
            sb.append(rowValues.joinToString(delimiter.toString()))
            sb.append("\n")
        }

        sb.toString().trimEnd('\n')
    }

    private fun flattenObject(prefix: String, obj: JSONObject, target: MutableMap<String, String>) {
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val fullKey = if (prefix.isEmpty()) key else "$prefix.$key"
            val value = obj.opt(key)
            when (value) {
                is JSONObject -> flattenObject(fullKey, value, target)
                is JSONArray -> target[fullKey] = value.toString()
                JSONObject.NULL -> target[fullKey] = ""
                else -> target[fullKey] = value?.toString() ?: ""
            }
        }
    }

    private fun escapeCsv(value: String, delimiter: Char): String {
        val mustQuote = value.contains(delimiter) || value.contains('"') || value.contains('\n') || value.contains('\r')
        return if (mustQuote) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    // ── CSV TO JSON ─────────────────────────────────────────────────────────

    fun csvToJson(csvString: String, delimiter: Char = ',', expandDots: Boolean = true): Result<String> = runCatching {
        val trimmed = csvString.trim()
        if (trimmed.isEmpty()) return@runCatching "[]"

        val rows = parseCsv(trimmed, delimiter)
        if (rows.isEmpty()) return@runCatching "[]"

        val headers = rows[0]
        if (headers.isEmpty()) return@runCatching "[]"

        val resultList = JSONArray()

        for (r in 1 until rows.size) {
            val row = rows[r]
            // Skip trailing empty row
            if (row.size == 1 && row[0].isEmpty()) continue

            if (expandDots) {
                val rootObj = JSONObject()
                for (c in headers.indices) {
                    val header = headers[c]
                    val rawVal = if (c < row.size) row[c] else ""
                    val parsedVal = inferType(rawVal)
                    setNestedValue(rootObj, header, parsedVal)
                }
                resultList.put(rootObj)
            } else {
                val flatObj = JSONObject()
                for (c in headers.indices) {
                    val header = headers[c]
                    val rawVal = if (c < row.size) row[c] else ""
                    flatObj.put(header, inferType(rawVal))
                }
                resultList.put(flatObj)
            }
        }

        resultList.toString(2)
    }

    private fun parseCsv(csv: String, delimiter: Char): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val curRow = mutableListOf<String>()
        val curVal = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < csv.length) {
            val c = csv[i]
            when {
                inQuotes -> {
                    if (c == '"') {
                        if (i + 1 < csv.length && csv[i + 1] == '"') {
                            curVal.append('"')
                            i++
                        } else {
                            inQuotes = false
                        }
                    } else {
                        curVal.append(c)
                    }
                }
                c == '"' -> {
                    inQuotes = true
                }
                c == delimiter -> {
                    curRow.add(curVal.toString())
                    curVal.clear()
                }
                c == '\r' -> {
                    if (i + 1 < csv.length && csv[i + 1] == '\n') {
                        i++
                    }
                    curRow.add(curVal.toString())
                    curVal.clear()
                    rows.add(curRow.toList())
                    curRow.clear()
                }
                c == '\n' -> {
                    curRow.add(curVal.toString())
                    curVal.clear()
                    rows.add(curRow.toList())
                    curRow.clear()
                }
                else -> {
                    curVal.append(c)
                }
            }
            i++
        }

        if (curVal.isNotEmpty() || curRow.isNotEmpty()) {
            curRow.add(curVal.toString())
            rows.add(curRow.toList())
        }

        return rows
    }

    private fun setNestedValue(obj: JSONObject, path: String, value: Any?) {
        val parts = path.split('.')
        var cur = obj
        for (i in 0 until parts.size - 1) {
            val part = parts[i]
            val next = cur.optJSONObject(part) ?: JSONObject().also { cur.put(part, it) }
            cur = next
        }
        cur.put(parts.last(), value ?: JSONObject.NULL)
    }

    private fun inferType(raw: String): Any? {
        val s = raw.trim()
        if (s.isEmpty()) return ""
        if (s.equals("null", ignoreCase = true)) return JSONObject.NULL
        if (s.equals("true", ignoreCase = true)) return true
        if (s.equals("false", ignoreCase = true)) return false
        val intVal = s.toLongOrNull()
        if (intVal != null) {
            return if (intVal in Int.MIN_VALUE..Int.MAX_VALUE) intVal.toInt() else intVal
        }
        val doubleVal = s.toDoubleOrNull()
        if (doubleVal != null && !doubleVal.isNaN() && !doubleVal.isInfinite()) {
            return doubleVal
        }
        return raw
    }

    // ── JSON TO YAML ────────────────────────────────────────────────────────

    fun jsonToYaml(jsonString: String): Result<String> = runCatching {
        val trimmed = jsonString.trim()
        if (trimmed.isEmpty()) return@runCatching ""

        val sb = StringBuilder()
        if (trimmed.startsWith("[")) {
            val arr = JSONArray(trimmed)
            formatYamlArray(arr, 0, sb)
        } else if (trimmed.startsWith("{")) {
            val obj = JSONObject(trimmed)
            formatYamlObject(obj, 0, sb)
        } else {
            throw IllegalArgumentException("Input must be a valid JSON object or array")
        }

        sb.toString().trimEnd('\n')
    }

    private fun formatYamlObject(obj: JSONObject, indent: Int, sb: StringBuilder) {
        val indentStr = "  ".repeat(indent)
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = obj.opt(key)
            val safeKey = formatYamlKey(key)

            when (value) {
                is JSONObject -> {
                    sb.append("$indentStr$safeKey:\n")
                    formatYamlObject(value, indent + 1, sb)
                }
                is JSONArray -> {
                    sb.append("$indentStr$safeKey:\n")
                    formatYamlArray(value, indent + 1, sb)
                }
                JSONObject.NULL -> {
                    sb.append("$indentStr$safeKey: null\n")
                }
                else -> {
                    sb.append("$indentStr$safeKey: ${formatYamlScalar(value)}\n")
                }
            }
        }
    }

    private fun formatYamlArray(arr: JSONArray, indent: Int, sb: StringBuilder) {
        val indentStr = "  ".repeat(indent)
        for (i in 0 until arr.length()) {
            val value = arr.opt(i)
            when (value) {
                is JSONObject -> {
                    sb.append("$indentStr- ")
                    val innerSb = StringBuilder()
                    formatYamlObject(value, indent + 1, innerSb)
                    val trimmedFirstLine = innerSb.toString().trimStart()
                    sb.append(trimmedFirstLine)
                }
                is JSONArray -> {
                    sb.append("$indentStr-\n")
                    formatYamlArray(value, indent + 1, sb)
                }
                JSONObject.NULL -> {
                    sb.append("$indentStr- null\n")
                }
                else -> {
                    sb.append("$indentStr- ${formatYamlScalar(value)}\n")
                }
            }
        }
    }

    private fun formatYamlKey(key: String): String {
        return if (key.contains(":") || key.contains(" ") || key.contains("#") || key.startsWith("-")) {
            "\"" + key.replace("\"", "\\\"") + "\""
        } else {
            key
        }
    }

    private fun formatYamlScalar(value: Any?): String {
        if (value == null || value == JSONObject.NULL) return "null"
        return when (value) {
            is Boolean -> value.toString()
            is Number -> value.toString()
            is String -> {
                if (value.contains("\n") || value.contains(":") || value.contains("#") ||
                    value.startsWith("@") || value.startsWith("`") || value.startsWith("%") ||
                    value.isEmpty() || value.equals("true", true) || value.equals("false", true) ||
                    value.equals("null", true) || value.toDoubleOrNull() != null
                ) {
                    "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""
                } else {
                    value
                }
            }
            else -> value.toString()
        }
    }

    // ── YAML TO JSON ────────────────────────────────────────────────────────

    fun yamlToJson(yamlString: String): Result<String> = runCatching {
        val trimmed = yamlString.trim()
        if (trimmed.isEmpty()) return@runCatching "{}"

        val lines = trimmed.lines()
            .map { it.trimEnd() }
            .filter { it.isNotBlank() && !it.trimStart().startsWith("#") }

        if (lines.isEmpty()) return@runCatching "{}"

        val parsed = parseYamlBlock(lines, 0).first
        when (parsed) {
            is JSONObject -> parsed.toString(2)
            is JSONArray -> parsed.toString(2)
            else -> JSONObject().put("value", parsed).toString(2)
        }
    }

    private fun parseYamlBlock(lines: List<String>, startIdx: Int): Pair<Any, Int> {
        if (startIdx >= lines.size) return Pair(JSONObject(), startIdx)

        val firstLine = lines[startIdx]
        val baseIndent = getIndent(firstLine)
        val isList = firstLine.trimStart().startsWith("-")

        if (isList) {
            val jsonArr = JSONArray()
            var idx = startIdx
            while (idx < lines.size) {
                val line = lines[idx]
                val indent = getIndent(line)
                if (indent < baseIndent) break
                if (indent == baseIndent && line.trimStart().startsWith("-")) {
                    val content = line.trimStart().removePrefix("-").trim()
                    if (content.isEmpty()) {
                        // Sub block
                        val (child, nextIdx) = parseYamlBlock(lines, idx + 1)
                        jsonArr.put(child)
                        idx = nextIdx
                    } else if (content.contains(":") && !content.startsWith("\"") && !content.startsWith("'")) {
                        // Inline object in list item: "- key: value"
                        val synthLines = mutableListOf<String>()
                        synthLines.add(" ".repeat(indent + 2) + content)
                        var peek = idx + 1
                        while (peek < lines.size && getIndent(lines[peek]) > indent) {
                            synthLines.add(lines[peek])
                            peek++
                        }
                        val (childObj, _) = parseYamlBlock(synthLines, 0)
                        jsonArr.put(childObj)
                        idx = peek
                    } else {
                        jsonArr.put(parseYamlValue(content))
                        idx++
                    }
                } else {
                    idx++
                }
            }
            return Pair(jsonArr, idx)
        } else {
            val jsonObj = JSONObject()
            var idx = startIdx
            while (idx < lines.size) {
                val line = lines[idx]
                val indent = getIndent(line)
                if (indent < baseIndent) break
                if (indent == baseIndent) {
                    val colonIdx = line.indexOf(':')
                    if (colonIdx != -1) {
                        val key = line.substring(0, colonIdx).trim().removeSurrounding("\"").removeSurrounding("'")
                        val rest = line.substring(colonIdx + 1).trim()
                        if (rest.isEmpty()) {
                            // Sub-structure follows
                            if (idx + 1 < lines.size && getIndent(lines[idx + 1]) > baseIndent) {
                                val (child, nextIdx) = parseYamlBlock(lines, idx + 1)
                                jsonObj.put(key, child)
                                idx = nextIdx
                            } else {
                                jsonObj.put(key, JSONObject.NULL)
                                idx++
                            }
                        } else {
                            jsonObj.put(key, parseYamlValue(rest))
                            idx++
                        }
                    } else {
                        idx++
                    }
                } else {
                    idx++
                }
            }
            return Pair(jsonObj, idx)
        }
    }

    private fun getIndent(line: String): Int {
        var count = 0
        for (c in line) {
            if (c == ' ') count++ else break
        }
        return count
    }

    private fun parseYamlValue(str: String): Any? {
        val s = str.trim()
        if (s.isEmpty() || s == "~" || s.equals("null", true)) return JSONObject.NULL
        if (s.equals("true", true)) return true
        if (s.equals("false", true)) return false
        if (s.startsWith("\"") && s.endsWith("\"") && s.length >= 2) {
            return s.substring(1, s.length - 1).replace("\\\"", "\"").replace("\\n", "\n")
        }
        if (s.startsWith("'") && s.endsWith("'") && s.length >= 2) {
            return s.substring(1, s.length - 1)
        }
        val intVal = s.toLongOrNull()
        if (intVal != null) {
            return if (intVal in Int.MIN_VALUE..Int.MAX_VALUE) intVal.toInt() else intVal
        }
        val doubleVal = s.toDoubleOrNull()
        if (doubleVal != null && !doubleVal.isNaN() && !doubleVal.isInfinite()) {
            return doubleVal
        }
        return s
    }
}
