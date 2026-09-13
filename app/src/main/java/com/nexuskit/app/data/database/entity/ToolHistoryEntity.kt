package com.nexuskit.app.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nexuskit.app.domain.model.ToolHistoryItem

/**
 * Room entity that records every time a tool is opened.
 * One row per open event — not deduplicated at insert time.
 * Deduplication happens at query time via GROUP BY in the DAO.
 *
 * Index on [toolId] speeds up per-tool history queries.
 * Index on [timestamp] speeds up the recent-history sort.
 */
@Entity(
    tableName = "tool_history",
    indices = [
        Index(value = ["tool_id"]),
        Index(value = ["timestamp"])
    ]
)
data class ToolHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "tool_id")
    val toolId: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long          // System.currentTimeMillis() at time of tool open
)

// ── Mappers (data layer only — domain model does NOT import this) ─────────────

fun ToolHistoryEntity.toDomain(): ToolHistoryItem = ToolHistoryItem(
    toolId    = toolId,
    timestamp = timestamp
)

fun ToolHistoryItem.toEntity(): ToolHistoryEntity = ToolHistoryEntity(
    toolId    = toolId,
    timestamp = timestamp
)
