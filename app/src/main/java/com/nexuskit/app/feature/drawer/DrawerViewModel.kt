package com.nexuskit.app.feature.drawer

import com.nexuskit.app.core.base.BaseViewModel
import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.domain.model.CategoryInfo
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.domain.usecase.preferences.GetUserPreferencesUseCase
import com.nexuskit.app.domain.usecase.tool.GetCategoriesUseCase
import com.nexuskit.app.domain.usecase.tool.GetRecentToolsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ── UI State ─────────────────────────────────────────────────────────────────

data class DrawerUiState(
    val appName: String = "NexusKit",
    val recentTools: List<ToolInfo> = emptyList(),
    val allCategories: List<CategoryInfo> = emptyList(),
    val searchQuery: String = "",
    val displayedCategories: List<CategoryInfo> = filterCategories(allCategories, searchQuery),
    val displayedRecentTools: List<ToolInfo> = filterRecentTools(recentTools, searchQuery),
    val hasNoResults: Boolean = searchQuery.isNotBlank() && displayedCategories.isEmpty() && displayedRecentTools.isEmpty()
) {
    companion object {
        fun filterCategories(categories: List<CategoryInfo>, query: String): List<CategoryInfo> {
            val nonEmpty = categories.filter { it.tools.isNotEmpty() }
            if (query.isBlank()) return nonEmpty
            val q = query.trim().lowercase()
            return nonEmpty.mapNotNull { cat ->
                val matchedTools = cat.tools.filter { tool ->
                    tool.name.lowercase().contains(q) ||
                    tool.description.lowercase().contains(q) ||
                    tool.searchKeywords.any { it.contains(q) }
                }
                if (matchedTools.isEmpty()) null
                else cat.copy(tools = matchedTools)
            }
        }

        fun filterRecentTools(recentTools: List<ToolInfo>, query: String): List<ToolInfo> {
            if (query.isBlank()) return recentTools
            val q = query.trim().lowercase()
            return recentTools.filter { tool ->
                tool.name.lowercase().contains(q) ||
                tool.searchKeywords.any { it.contains(q) }
            }
        }
    }
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class DrawerViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getRecentToolsUseCase: GetRecentToolsUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(DrawerUiState())
    val uiState: StateFlow<DrawerUiState> = _uiState.asStateFlow()

    init {
        collectDrawerData()
    }

    private fun collectDrawerData() {
        launchSafe {
            combine(
                getCategoriesUseCase(),
                getRecentToolsUseCase(),
                getUserPreferencesUseCase()
            ) { catResource, recentResource, prefsResource ->
                val appName = (prefsResource as? Resource.Success)?.data?.appName ?: "NexusKit"
                val recent = (recentResource as? Resource.Success)?.data ?: emptyList()
                val cats = (catResource as? Resource.Success)?.data ?: emptyList()
                val query = _uiState.value.searchQuery

                DrawerUiState(
                    appName = appName,
                    recentTools = recent,
                    allCategories = cats,
                    searchQuery = query
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                displayedCategories = DrawerUiState.filterCategories(current.allCategories, query),
                displayedRecentTools = DrawerUiState.filterRecentTools(current.recentTools, query),
                hasNoResults = query.isNotBlank() &&
                        DrawerUiState.filterCategories(current.allCategories, query).isEmpty() &&
                        DrawerUiState.filterRecentTools(current.recentTools, query).isEmpty()
            )
        }
    }

    fun onClearSearch() {
        onSearchQueryChanged("")
    }

    override fun handleException(throwable: Throwable) {
        // Drawer errors are non-critical — silently swallow
    }
}
