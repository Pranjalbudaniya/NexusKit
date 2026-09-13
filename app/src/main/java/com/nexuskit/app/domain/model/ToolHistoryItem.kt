package com.nexuskit.app.domain.model

/**
 * Records that a tool was opened at a given time.
 * Used to populate "Recent" tools in the side drawer (Spec 11).
 * Stored in Room via ToolHistoryEntity (Spec 03).
 */
data class ToolHistoryItem(
    val toolId: String,
    val timestamp: Long          // System.currentTimeMillis() at time of opening
)
