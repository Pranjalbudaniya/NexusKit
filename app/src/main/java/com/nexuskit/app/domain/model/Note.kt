package com.nexuskit.app.domain.model

/**
 * Domain model representing a text note created in the Text Editor tool.
 */
data class Note(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val isPinned: Boolean = false,
    val colorTag: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
