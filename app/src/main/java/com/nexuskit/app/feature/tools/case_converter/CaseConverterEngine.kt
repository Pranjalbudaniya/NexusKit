package com.nexuskit.app.feature.tools.case_converter

import java.util.Locale

data class ConvertedCase(
    val id: String,
    val title: String,
    val description: String,
    val output: String
)

object CaseConverterEngine {

    private val SPLIT_REGEX = Regex("[\\s_\\-]+|(?<=[a-z])(?=[A-Z])")

    private fun extractWords(text: String): List<String> {
        if (text.isBlank()) return emptyList()
        return text.trim().split(SPLIT_REGEX).filter { it.isNotBlank() }
    }

    fun convertAll(text: String): List<ConvertedCase> {
        if (text.isEmpty()) return emptyList()

        val words = extractWords(text)

        val uppercase = text.uppercase(Locale.ROOT)
        val lowercase = text.lowercase(Locale.ROOT)
        val titleCase = words.joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.titlecase(Locale.ROOT) }
        }
        val sentenceCase = text.lowercase().replaceFirstChar { it.titlecase(Locale.ROOT) }

        val camelCase = if (words.isEmpty()) "" else {
            words.first().lowercase() + words.drop(1).joinToString("") { word ->
                word.lowercase().replaceFirstChar { it.titlecase(Locale.ROOT) }
            }
        }

        val pascalCase = words.joinToString("") { word ->
            word.lowercase().replaceFirstChar { it.titlecase(Locale.ROOT) }
        }

        val snakeCase = words.joinToString("_") { it.lowercase() }
        val constantCase = words.joinToString("_") { it.uppercase() }
        val kebabCase = words.joinToString("-") { it.lowercase() }
        val dotCase = words.joinToString(".") { it.lowercase() }

        val alternating = text.mapIndexed { idx, c ->
            if (idx % 2 == 0) c.lowercaseChar() else c.uppercaseChar()
        }.joinToString("")

        val reversed = text.reversed()

        return listOf(
            ConvertedCase("upper", "UPPERCASE", "All letters capitalized", uppercase),
            ConvertedCase("lower", "lowercase", "All letters in lowercase", lowercase),
            ConvertedCase("title", "Title Case", "First Letter Of Each Word Capitalized", titleCase),
            ConvertedCase("sentence", "Sentence case", "First letter capitalized", sentenceCase),
            ConvertedCase("camel", "camelCase", "Standard programming camelCase", camelCase),
            ConvertedCase("pascal", "PascalCase", "Standard programming PascalCase", pascalCase),
            ConvertedCase("snake", "snake_case", "Words separated by underscores", snakeCase),
            ConvertedCase("constant", "CONSTANT_CASE", "Uppercase separated by underscores", constantCase),
            ConvertedCase("kebab", "kebab-case", "Words separated by hyphens", kebabCase),
            ConvertedCase("dot", "dot.case", "Words separated by dots", dotCase),
            ConvertedCase("alt", "aLtErNaTiNg cAsE", "Alternating capital and small letters", alternating),
            ConvertedCase("reverse", "Reverse Text", "String characters reversed", reversed)
        )
    }
}
