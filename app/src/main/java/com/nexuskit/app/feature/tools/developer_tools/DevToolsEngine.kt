package com.nexuskit.app.feature.tools.developer_tools

import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern

enum class DevToolTab {
    JSON_FORMATTER, REGEX_TESTER, CRON_BUILDER, MARKDOWN_PREVIEW, HTTP_CODES, SQL_FORMATTER, MINIFIERS, UNICODE_LOOKUP
}

data class HttpCodeInfo(
    val code: Int,
    val phrase: String,
    val category: String,
    val description: String
)

data class RegexMatchResult(
    val matchText: String,
    val range: IntRange,
    val groups: List<String>
)

object DevToolsEngine {

    val httpCodes = listOf(
        HttpCodeInfo(200, "OK", "2xx Success", "Standard response for successful HTTP requests."),
        HttpCodeInfo(201, "Created", "2xx Success", "Request succeeded and led to the creation of a new resource."),
        HttpCodeInfo(204, "No Content", "2xx Success", "Request succeeded with no content in the response payload."),
        HttpCodeInfo(301, "Moved Permanently", "3xx Redirection", "Resource has been assigned a new permanent URI."),
        HttpCodeInfo(302, "Found (Temporary Redirect)", "3xx Redirection", "Resource resides temporarily under a different URI."),
        HttpCodeInfo(304, "Not Modified", "3xx Redirection", "Cached version is still valid; no retransmission needed."),
        HttpCodeInfo(400, "Bad Request", "4xx Client Error", "Server cannot process request due to client error (malformed syntax)."),
        HttpCodeInfo(401, "Unauthorized", "4xx Client Error", "Authentication is required and has failed or not been provided."),
        HttpCodeInfo(403, "Forbidden", "4xx Client Error", "Server understood request but refuses to authorize it."),
        HttpCodeInfo(404, "Not Found", "4xx Client Error", "Requested resource could not be found on the server."),
        HttpCodeInfo(405, "Method Not Allowed", "4xx Client Error", "HTTP method is not allowed for the requested resource."),
        HttpCodeInfo(408, "Request Timeout", "4xx Client Error", "Server timed out waiting for the request."),
        HttpCodeInfo(409, "Conflict", "4xx Client Error", "Request conflict with current state of the resource."),
        HttpCodeInfo(422, "Unprocessable Entity", "4xx Client Error", "Semantic errors in request data despite valid syntax."),
        HttpCodeInfo(429, "Too Many Requests", "4xx Client Error", "Client has sent too many requests in a given time (Rate Limited)."),
        HttpCodeInfo(500, "Internal Server Error", "5xx Server Error", "Generic error message when an unexpected condition occurred on the server."),
        HttpCodeInfo(502, "Bad Gateway", "5xx Server Error", "Server received an invalid response from upstream gateway."),
        HttpCodeInfo(503, "Service Unavailable", "5xx Server Error", "Server is currently unable to handle the request due to maintenance or overload."),
        HttpCodeInfo(504, "Gateway Timeout", "5xx Server Error", "Server did not receive timely response from upstream.")
    )

    fun formatJson(input: String, indentSpaces: Int = 2): Pair<Boolean, String> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return true to ""
        return try {
            if (trimmed.startsWith("{")) {
                val json = JSONObject(trimmed)
                true to json.toString(indentSpaces)
            } else if (trimmed.startsWith("[")) {
                val array = JSONArray(trimmed)
                true to array.toString(indentSpaces)
            } else {
                false to "Invalid JSON: Must start with '{' or '['"
            }
        } catch (e: Exception) {
            false to (e.message ?: "Invalid JSON syntax")
        }
    }

    fun minifyJson(input: String): Pair<Boolean, String> {
        return formatJson(input, 0)
    }

    fun testRegex(patternStr: String, sampleText: String, isCaseInsensitive: Boolean = false, isDotAll: Boolean = false): Pair<Boolean, List<RegexMatchResult>> {
        if (patternStr.isEmpty() || sampleText.isEmpty()) return true to emptyList()
        return try {
            var flags = 0
            if (isCaseInsensitive) flags = flags or Pattern.CASE_INSENSITIVE
            if (isDotAll) flags = flags or Pattern.DOTALL
            val pattern = Pattern.compile(patternStr, flags)
            val matcher = pattern.matcher(sampleText)
            val list = mutableListOf<RegexMatchResult>()
            while (matcher.find()) {
                val groups = mutableListOf<String>()
                for (g in 1..matcher.groupCount()) {
                    groups.add(matcher.group(g) ?: "")
                }
                list.add(
                    RegexMatchResult(
                        matchText = matcher.group(),
                        range = matcher.start()..matcher.end(),
                        groups = groups
                    )
                )
            }
            true to list
        } catch (e: Exception) {
            false to emptyList()
        }
    }

    fun explainCron(minute: String, hour: String, dayOfMonth: String, month: String, dayOfWeek: String): String {
        return buildString {
            append("Runs ")
            if (minute == "*" && hour == "*") {
                append("every minute")
            } else if (minute.startsWith("*/")) {
                append("every ${minute.removePrefix("*/")} minutes")
            } else if (minute == "0" && hour == "*") {
                append("at the start of every hour")
            } else {
                append("at minute $minute of hour $hour")
            }

            if (dayOfMonth != "*") append(", on day $dayOfMonth of the month")
            if (month != "*") append(", in month $month")
            if (dayOfWeek != "*") append(", on day $dayOfWeek of the week")
            append(".")
        }
    }

    fun formatSql(rawSql: String): String {
        val keywords = listOf("SELECT", "FROM", "WHERE", "GROUP BY", "ORDER BY", "HAVING", "LIMIT", "LEFT JOIN", "RIGHT JOIN", "INNER JOIN", "JOIN", "ON", "INSERT INTO", "VALUES", "UPDATE", "SET", "DELETE FROM", "UNION", "CREATE TABLE", "ALTER TABLE", "DROP TABLE")
        var formatted = rawSql.trim()
        keywords.forEach { kw ->
            val regex = Regex("(?i)\\b$kw\\b")
            formatted = formatted.replace(regex, "\n$kw")
        }
        return formatted.lines().map { it.trim() }.filter { it.isNotEmpty() }.joinToString("\n")
    }

    fun minifyCss(css: String): String {
        return css.replace(Regex("/\\*.*?\\*/", RegexOption.DOT_MATCHES_ALL), "")
            .replace(Regex("\\s+"), " ")
            .replace(Regex("\\s*([{};:,])\\s*"), "$1")
            .trim()
    }
}
