package com.nexuskit.app.feature.tools.lorem_generator

import kotlin.random.Random

enum class LoremType(val label: String) {
    PARAGRAPHS("Paragraphs"),
    SENTENCES("Sentences"),
    WORDS("Words"),
    LIST_ITEMS("List Items")
}

enum class LoremTheme(val label: String) {
    CLASSIC("Classic Latin"),
    DEVELOPER("Tech & Developer"),
    CYBERSECURITY("Cybersecurity"),
    HIPSTER("Hipster & Café")
}

data class LoremGeneratedResult(
    val text: String,
    val wordCount: Int,
    val charCount: Int,
    val readingTimeSeconds: Int
)

object LoremEngine {

    private val CLASSIC_WORDS = listOf(
        "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit", "sed",
        "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore", "magna", "aliqua",
        "enim", "ad", "minim", "veniam", "quis", "nostrud", "exercitation", "ullamco", "laboris",
        "nisi", "aliquip", "ex", "ea", "commodo", "consequat", "duis", "aute", "irure", "in",
        "reprehenderit", "voluptate", "velit", "esse", "cillum", "fugiat", "nulla", "pariatur",
        "excepteur", "sint", "occaecat", "cupidatat", "non", "proident", "sunt", "culpa", "qui",
        "officia", "deserunt", "mollit", "anim", "id", "est", "laborum", "curabitur", "pretium",
        "tincidunt", "lacus", "sollicitudin", "felis", "pellentesque", "habitant", "morbi",
        "tristique", "senectus", "netus", "fames", "ac", "egestas", "semper", "interdum"
    )

    private val DEV_WORDS = listOf(
        "kubernetes", "docker", "microservice", "graphql", "rest", "api", "backend", "frontend",
        "database", "redis", "postgres", "kafka", "reactive", "asynchronous", "coroutine", "thread",
        "concurrency", "deployment", "pipeline", "ci-cd", "cluster", "node", "serverless", "lambda",
        "framework", "architecture", "monolith", "refactor", "pull-request", "merge", "git", "branch",
        "commit", "dependency", "injection", "singleton", "interface", "polymorphism", "payload",
        "endpoint", "websocket", "protocol", "protobuf", "grpc", "cache", "latency", "throughput",
        "scalability", "load-balancer", "proxy", "oauth", "jwt", "session", "middleware", "stacktrace"
    )

    private val CYBER_WORDS = listOf(
        "zero-day", "exploit", "firewall", "buffer-overflow", "injection", "xss", "csrf", "malware",
        "ransomware", "trojan", "cryptography", "encryption", "cipher", "hash", "sha256", "rsa",
        "elliptic-curve", "air-gap", "penetration-testing", "vulnerability", "audit", "sandbox",
        "privilege-escalation", "rootkit", "backdoor", "phishing", "honeypot", "ddos", "mitigation",
        "soc", "siem", "forensics", "steganography", "exfiltration", "payload", "obfuscation", "antivirus"
    )

    private val HIPSTER_WORDS = listOf(
        "artisanal", "pour-over", "cold-brew", "single-origin", "espresso", "latte", "kombucha",
        "sourdough", "farm-to-table", "sustainable", "organic", "fair-trade", "vintage", "retro",
        "bespoke", "craft", "aesthetic", "minimalist", "nordic", "matcha", "avocado", "roastery",
        "tasting-notes", "acidity", "barista", "filter", "aeropress", "chemex", "small-batch", "roast"
    )

    fun generate(
        type: LoremType,
        count: Int,
        theme: LoremTheme,
        startWithLorem: Boolean,
        htmlWrap: Boolean
    ): LoremGeneratedResult {
        val wordBank = when (theme) {
            LoremTheme.CLASSIC -> CLASSIC_WORDS
            LoremTheme.DEVELOPER -> DEV_WORDS
            LoremTheme.CYBERSECURITY -> CYBER_WORDS
            LoremTheme.HIPSTER -> HIPSTER_WORDS
        }

        val clampedCount = count.coerceIn(1, 100)
        val random = Random(System.currentTimeMillis())

        val text = when (type) {
            LoremType.WORDS -> {
                val words = mutableListOf<String>()
                if (startWithLorem && theme == LoremTheme.CLASSIC) {
                    val prefix = listOf("Lorem", "ipsum", "dolor", "sit", "amet")
                    words.addAll(prefix.take(clampedCount))
                }
                while (words.size < clampedCount) {
                    words.add(wordBank[random.nextInt(wordBank.size)])
                }
                words.joinToString(" ")
            }
            LoremType.SENTENCES -> {
                val sentences = (1..clampedCount).mapIndexed { idx, _ ->
                    generateSentence(wordBank, random, idx == 0 && startWithLorem && theme == LoremTheme.CLASSIC)
                }
                if (htmlWrap) sentences.joinToString("\n") { "<p>$it</p>" } else sentences.joinToString(" ")
            }
            LoremType.PARAGRAPHS -> {
                val paragraphs = (1..clampedCount).mapIndexed { idx, _ ->
                    val numSentences = random.nextInt(3, 7)
                    val sentences = (0 until numSentences).map { sIdx ->
                        generateSentence(wordBank, random, idx == 0 && sIdx == 0 && startWithLorem && theme == LoremTheme.CLASSIC)
                    }
                    val body = sentences.joinToString(" ")
                    if (htmlWrap) "<p>$body</p>" else body
                }
                paragraphs.joinToString("\n\n")
            }
            LoremType.LIST_ITEMS -> {
                val items = (1..clampedCount).map {
                    val wordsCount = random.nextInt(3, 8)
                    val words = (1..wordsCount).map { wordBank[random.nextInt(wordBank.size)] }
                    val itemText = words.joinToString(" ").replaceFirstChar { it.uppercase() }
                    if (htmlWrap) "  <li>$itemText</li>" else "• $itemText"
                }
                if (htmlWrap) "<ul>\n" + items.joinToString("\n") + "\n</ul>" else items.joinToString("\n")
            }
        }

        val words = text.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val wordCount = words.size
        val charCount = text.length
        val readingTimeSec = Math.max(1, (wordCount / 3.3).toInt()) // ~200 wpm

        return LoremGeneratedResult(
            text = text,
            wordCount = wordCount,
            charCount = charCount,
            readingTimeSeconds = readingTimeSec
        )
    }

    private fun generateSentence(words: List<String>, random: Random, forceLoremStart: Boolean): String {
        val length = random.nextInt(6, 16)
        val selected = mutableListOf<String>()

        if (forceLoremStart) {
            selected.addAll(listOf("lorem", "ipsum", "dolor", "sit", "amet"))
        }

        while (selected.size < length) {
            selected.add(words[random.nextInt(words.size)])
        }

        val sentence = selected.joinToString(" ")
        return sentence.replaceFirstChar { it.uppercase() } + "."
    }
}
