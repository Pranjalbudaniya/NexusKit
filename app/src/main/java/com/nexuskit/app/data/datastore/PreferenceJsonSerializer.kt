package com.nexuskit.app.data.datastore

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Serializes and deserializes complex preference types to/from JSON strings.
 * Used only by NexusKitDataStore — no other class should use this directly.
 *
 * All decode functions are safe:
 *   - null input returns the empty default.
 *   - Malformed JSON returns the empty default (never throws).
 */
internal object PreferenceJsonSerializer {

    private val json = Json { ignoreUnknownKeys = true }

    // ── List<String> ──────────────────────────────────────────────────────────

    fun encodeStringList(list: List<String>): String =
        json.encodeToString(list)

    fun decodeStringList(value: String?): List<String> =
        value?.let { runCatching { json.decodeFromString<List<String>>(it) }.getOrNull() }
            ?: emptyList()

    // ── Map<String, String> ───────────────────────────────────────────────────

    fun encodeStringMap(map: Map<String, String>): String =
        json.encodeToString(map)

    fun decodeStringMap(value: String?): Map<String, String> =
        value?.let { runCatching { json.decodeFromString<Map<String, String>>(it) }.getOrNull() }
            ?: emptyMap()

    // ── Map<String, Boolean> ──────────────────────────────────────────────────

    fun encodeBooleanMap(map: Map<String, Boolean>): String =
        json.encodeToString(map)

    fun decodeBooleanMap(value: String?): Map<String, Boolean> =
        value?.let { runCatching { json.decodeFromString<Map<String, Boolean>>(it) }.getOrNull() }
            ?: emptyMap()

    // ── Map<String, Int> ──────────────────────────────────────────────────────

    fun encodeIntMap(map: Map<String, Int>): String =
        json.encodeToString(map)

    fun decodeIntMap(value: String?): Map<String, Int> =
        value?.let { runCatching { json.decodeFromString<Map<String, Int>>(it) }.getOrNull() }
            ?: emptyMap()

    // ── Map<String, List<String>> ─────────────────────────────────────────────

    fun encodeStringListMap(map: Map<String, List<String>>): String =
        json.encodeToString(map)

    fun decodeStringListMap(value: String?): Map<String, List<String>> =
        value?.let { runCatching { json.decodeFromString<Map<String, List<String>>>(it) }.getOrNull() }
            ?: emptyMap()
}
