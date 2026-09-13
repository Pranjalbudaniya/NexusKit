package com.nexuskit.app.feature.home

import com.nexuskit.app.core.base.BaseViewModel
import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.domain.model.CategoryInfo
import com.nexuskit.app.domain.model.PreferenceUpdate
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.domain.model.UserPreferences
import com.nexuskit.app.domain.usecase.preferences.GetUserPreferencesUseCase
import com.nexuskit.app.domain.usecase.preferences.UpdatePreferencesUseCase
import com.nexuskit.app.domain.usecase.tool.GetCategoriesUseCase
import com.nexuskit.app.domain.usecase.tool.GetFavoriteToolsUseCase
import com.nexuskit.app.domain.usecase.tool.HideToolParams
import com.nexuskit.app.domain.usecase.tool.HideToolUseCase
import com.nexuskit.app.domain.usecase.tool.ToggleFavoriteParams
import com.nexuskit.app.domain.usecase.tool.ToggleFavoriteUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolOpenedUseCase
import com.nexuskit.app.domain.usecase.tool.TrackToolParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ── UI State ─────────────────────────────────────────────────────────────────

data class HomeUiState(
    val categories: List<CategoryInfo> = emptyList(),
    val favouriteTools: List<ToolInfo> = emptyList(),
    val preferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = true,
    val error: String? = null,
    /** Non-null when long-press bottom sheet is open. */
    val selectedToolForInfo: ToolInfo? = null,
    /** True when user is in rearrange mode (tapped Edit on a category). */
    val isRearrangeMode: Boolean = false,
    /** ID of category being rearranged, null if none. */
    val rearrangingCategoryId: String? = null
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getFavoriteToolsUseCase: GetFavoriteToolsUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val hideToolUseCase: HideToolUseCase,
    private val updatePreferencesUseCase: UpdatePreferencesUseCase,
    private val trackToolOpenedUseCase: TrackToolOpenedUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        collectHomeData()
    }

    /**
     * Combines three flows into a single HomeUiState emission.
     * Re-emits on ANY preference, category, or favourites change.
     */
    private fun collectHomeData() {
        launchSafe {
            combine(
                getCategoriesUseCase(),
                getFavoriteToolsUseCase(),
                getUserPreferencesUseCase()
            ) { catResource, favResource, prefsResource ->
                HomeUiState(
                    categories      = (catResource as? Resource.Success)?.data ?: emptyList(),
                    favouriteTools  = (favResource as? Resource.Success)?.data ?: emptyList(),
                    preferences     = (prefsResource as? Resource.Success)?.data ?: UserPreferences(),
                    isLoading       = catResource is Resource.Loading,
                    error           = (catResource as? Resource.Error)?.message,
                    // Preserve transient UI state across re-emissions
                    selectedToolForInfo   = _uiState.value.selectedToolForInfo,
                    isRearrangeMode       = _uiState.value.isRearrangeMode,
                    rearrangingCategoryId = _uiState.value.rearrangingCategoryId
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    // ── Tool Info Bottom Sheet ─────────────────────────────────────────────────

    fun onToolLongPress(tool: ToolInfo) {
        _uiState.update { it.copy(selectedToolForInfo = tool) }
    }

    fun onDismissToolInfo() {
        _uiState.update { it.copy(selectedToolForInfo = null) }
    }

    // ── Favourites ─────────────────────────────────────────────────────────────

    fun onToggleFavourite(toolId: String) = launchSafe {
        toggleFavoriteUseCase(ToggleFavoriteParams(toolId))
    }

    // ── Hide Tool ─────────────────────────────────────────────────────────────

    fun onHideTool(toolId: String) {
        launchSafe {
            hideToolUseCase(HideToolParams(toolId))
        }
        _uiState.update { it.copy(selectedToolForInfo = null) }
    }

    // ── Category Expand / Collapse ────────────────────────────────────────────

    fun onToggleCategoryExpand(categoryId: String, currentlyExpanded: Boolean) = launchSafe {
        updatePreferencesUseCase(
            PreferenceUpdate.SetCategoryExpanded(categoryId, !currentlyExpanded)
        )
    }

    // ── Rearrange Mode ────────────────────────────────────────────────────────

    /**
     * Enters rearrange mode for a specific category.
     * Shows drag handles on tool items inside that category.
     */
    fun onEnterRearrangeMode(categoryId: String) {
        _uiState.update { it.copy(isRearrangeMode = true, rearrangingCategoryId = categoryId) }
    }

    fun onExitRearrangeMode() {
        _uiState.update { it.copy(isRearrangeMode = false, rearrangingCategoryId = null) }
    }

    // ── Tool Opened (tracking) ────────────────────────────────────────────────

    fun onToolOpened(toolId: String) = launchSafe {
        trackToolOpenedUseCase(TrackToolParams(toolId))
    }

    override fun handleException(throwable: Throwable) {
        _uiState.update { it.copy(error = throwable.message, isLoading = false) }
    }
}
