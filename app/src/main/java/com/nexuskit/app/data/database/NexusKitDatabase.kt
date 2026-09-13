package com.nexuskit.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nexuskit.app.data.database.dao.NoteDao
import com.nexuskit.app.data.database.dao.ToolHistoryDao
import com.nexuskit.app.data.database.entity.NoteEntity
import com.nexuskit.app.data.database.entity.ToolHistoryEntity

/**
 * NexusKit Room database.
 *
 * VERSION HISTORY:
 *   1  — Initial schema. Contains tool_history table only.
 *   2  — Added notes table for Text Editor tool.
 */
@Database(
    entities = [
        ToolHistoryEntity::class,
        NoteEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class NexusKitDatabase : RoomDatabase() {

    abstract fun toolHistoryDao(): ToolHistoryDao
    abstract fun noteDao(): NoteDao
}
