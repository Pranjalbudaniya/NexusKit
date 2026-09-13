package com.nexuskit.app.domain.repository

import com.nexuskit.app.domain.model.CategoryInfo
import com.nexuskit.app.domain.model.ToolInfo
import kotlinx.coroutines.flow.Flow

interface IToolRepository {

    /**
     * All available, non-hidden tools as a reactive stream.
     * Re-emits whenever the user's hidden tools preference changes.
     */
    fun getAllTools(): Flow<List<ToolInfo>>

    /**
     * All categories with their tools, fully merged with user preferences:
     *   • Category order     — from user settings (or registry default)
     *   • Tool order         — per-category user ordering (or registry default)
     *   • Hidden tools       — filtered out
     *   • Display mode       — per-category user choice applied
     *   • Expand state       — per-category user choice applied
     *   • Grid columns       — per-category user choice applied
     *
     * Re-emits whenever any relevant preference changes.
     * This is the primary data source for the home screen.
     */
    fun getCategoriesWithTools(): Flow<List<CategoryInfo>>

    /**
     * Favourite tools in their saved order. Max 8.
     * Re-emits when favourites or hidden tools change.
     */
    fun getFavoriteTools(): Flow<List<ToolInfo>>

    /**
     * The set of tool IDs the user has explicitly hidden.
     * Re-emits when hidden tools change.
     */
    fun getHiddenToolIds(): Flow<Set<String>>

    /**
     * Most recently opened distinct tool IDs, newest first.
     * Used to populate the "Recent" section in the side drawer (Spec 11).
     * Re-emits as new history is recorded.
     */
    fun getRecentToolIds(limit: Int = 10): Flow<List<String>>

    /**
     * Records that a tool was opened.
     * Inserts a history row and trims the table if needed.
     * Runs on IoDispatcher.
     */
    suspend fun trackToolOpened(toolId: String)

    /**
     * Synchronous text search across tool name, description, and keywords.
     * Filters out hidden tools using the current preferences snapshot.
     * Called from ParseSearchQueryUseCase (Spec 06).
     */
    suspend fun searchTools(query: String): List<ToolInfo>
}
