package com.nexuskit.app.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nexuskit.app.domain.model.Note

/**
 * Room entity representing notes in the text editor.
 */
@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["is_pinned"]),
        Index(value = ["updated_at"])
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "is_pinned", defaultValue = "0")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "color_tag", defaultValue = "0")
    val colorTag: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    isPinned = isPinned,
    colorTag = colorTag,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    isPinned = isPinned,
    colorTag = colorTag,
    createdAt = createdAt,
    updatedAt = updatedAt
)
