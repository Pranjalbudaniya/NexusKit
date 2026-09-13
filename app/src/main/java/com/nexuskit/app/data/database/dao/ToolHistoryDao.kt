package com.nexuskit.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nexuskit.app.data.database.entity.ToolHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolHistoryDao {

    /**
     * Returns the most recently opened distinct tools, deduplicated.
     * Each toolId appears only once, ordered by its most recent open time.
     * Used to populate the "Recent" section in the side drawer (Spec 11).
     */
    @Query("""
        SELECT tool_id 
        FROM tool_history 
        GROUP BY tool_id 
        ORDER BY MAX(timestamp) DESC 
        LIMIT :limit
    """)
    fun getRecentDistinctToolIds(limit: Int = 10): Flow<List<String>>

    /**
     * Returns the full raw history for a single tool, newest first.
     * Used by tool-specific screens to show their own history.
     */
    @Query("""
        SELECT * FROM tool_history 
        WHERE tool_id = :toolId 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    fun getHistoryForTool(toolId: String, limit: Int = 50): Flow<List<ToolHistoryEntity>>

    /**
     * Records a tool open event.
     * Inserts a new row every time — does not replace existing rows.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHistory(entity: ToolHistoryEntity)

    /**
     * Deletes all history rows for a specific tool.
     * Called when user clears history from Settings.
     */
    @Query("DELETE FROM tool_history WHERE tool_id = :toolId")
    suspend fun clearHistoryForTool(toolId: String)

    /**
     * Deletes entire history table.
     * Called from "Clear all history" in Settings.
     */
    @Query("DELETE FROM tool_history")
    suspend fun clearAllHistory()

    /**
     * Returns total number of history rows.
     * Used for periodic cleanup — trim rows above MAX_HISTORY_ROWS.
     */
    @Query("SELECT COUNT(*) FROM tool_history")
    suspend fun getTotalRowCount(): Int

    /**
     * Deletes oldest rows beyond [keepCount].
     * Keeps the table from growing unbounded.
     * Call this after every insertHistory() via the repository.
     */
    @Query("""
        DELETE FROM tool_history 
        WHERE id NOT IN (
            SELECT id FROM tool_history 
            ORDER BY timestamp DESC 
            LIMIT :keepCount
        )
    """)
    suspend fun trimHistory(keepCount: Int = 200)
}
