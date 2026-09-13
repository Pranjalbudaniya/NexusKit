package com.nexuskit.app.data.repository

import com.nexuskit.app.data.database.dao.ToolHistoryDao
import com.nexuskit.app.data.database.entity.ToolHistoryEntity
import com.nexuskit.app.data.datastore.NexusKitDataStore
import com.nexuskit.app.domain.model.CategoryInfo
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.domain.model.ToolRegistry
import com.nexuskit.app.domain.model.UserPreferences
import com.nexuskit.app.domain.repository.IToolRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class ToolRepositoryImpl @Inject constructor(
    private val dataStore: NexusKitDataStore,
    private val historyDao: ToolHistoryDao,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher
) : IToolRepository {

    override fun getAllTools(): Flow<List<ToolInfo>> =
        dataStore.userPreferences.map { prefs ->
            ToolRegistry.allTools.filter {
                it.isAvailable && it.id !in prefs.hiddenToolIds
            }
        }

    override fun getCategoriesWithTools(): Flow<List<CategoryInfo>> =
        dataStore.userPreferences.map { prefs ->
            buildCategoriesWithPreferences(prefs)
        }

    override fun getFavoriteTools(): Flow<List<ToolInfo>> =
        dataStore.userPreferences.map { prefs ->
            prefs.favoriteToolIds.mapNotNull { id ->
                ToolRegistry.getToolById(id)?.takeIf {
                    it.isAvailable && it.id !in prefs.hiddenToolIds
                }
            }
        }

    override fun getHiddenToolIds(): Flow<Set<String>> =
        dataStore.userPreferences.map { it.hiddenToolIds }

    override fun getRecentToolIds(limit: Int): Flow<List<String>> =
        historyDao.getRecentDistinctToolIds(limit)

    override suspend fun trackToolOpened(toolId: String) {
        withContext(ioDispatcher) {
            historyDao.insertHistory(
                ToolHistoryEntity(
                    toolId    = toolId,
                    timestamp = System.currentTimeMillis()
                )
            )
            historyDao.trimHistory(keepCount = 200)
        }
    }

    override suspend fun searchTools(query: String): List<ToolInfo> {
        val hidden = dataStore.userPreferences.first().hiddenToolIds
        return ToolRegistry.searchTools(query).filter { it.id !in hidden }
    }

    // ── Private ───────────────────────────────────────────────────────────────

    /**
     * Merges static ToolRegistry data with live UserPreferences.
     * Called inside getCategoriesWithTools() map block.
     */
    private fun buildCategoriesWithPreferences(prefs: UserPreferences): List<CategoryInfo> {
        val hidden = prefs.hiddenToolIds

        val categories = ToolRegistry.allCategories.map { cat ->

            // 1. Get tools for this category, filter hidden
            val baseTools = ToolRegistry.getToolsByCategory(cat.id)
                .filter { it.id !in hidden }

            // 2. Apply user-defined tool order if saved
            val orderedTools = prefs.toolOrderMap[cat.id]?.let { userOrder ->
                val indexMap = userOrder.withIndex().associate { (i, id) -> id to i }
                baseTools.sortedBy { tool -> indexMap[tool.id] ?: Int.MAX_VALUE }
            } ?: baseTools

            // 3. Apply per-category preferences
            cat.copy(
                displayMode = prefs.categoryDisplayModes[cat.id]   ?: cat.displayMode,
                isExpanded  = prefs.categoryExpandedStates[cat.id] ?: cat.isExpanded,
                gridColumns = prefs.gridColumns[cat.id]            ?: cat.gridColumns,
                tools       = orderedTools
            )
        }

        // 4. Apply user-defined category order if saved, else use registry default
        return if (prefs.categoryOrder.isNotEmpty()) {
            val indexMap = prefs.categoryOrder.withIndex().associate { (i, id) -> id to i }
            categories.sortedBy { cat -> indexMap[cat.id] ?: cat.order }
        } else {
            categories.sortedBy { it.order }
        }
    }
}
