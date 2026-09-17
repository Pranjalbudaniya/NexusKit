package com.nexuskit.app.feature.settings

import com.nexuskit.app.core.base.BaseViewModel
import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.domain.model.PreferenceUpdate
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.domain.model.UserPreferences
import com.nexuskit.app.domain.usecase.preferences.GetUserPreferencesUseCase
import com.nexuskit.app.domain.usecase.preferences.UpdatePreferencesUseCase
import com.nexuskit.app.domain.usecase.tool.GetHiddenToolsUseCase
import com.nexuskit.app.domain.usecase.tool.UnhideToolParams
import com.nexuskit.app.domain.usecase.tool.UnhideToolUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SettingsUiState(
    val preferences: UserPreferences = UserPreferences(),
    val hiddenTools: List<ToolInfo> = emptyList(),
    val isLoading: Boolean = true,
    /** Which bottom sheet is currently open. */
    val openSheet: SettingsSheet? = null
)

enum class SettingsSheet {
    THEME, THEME_MODE, CARD_SHAPE, FAV_SHAPE, STATUS_BAR, HIDDEN_TOOLS, APP_NAME
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val updatePreferencesUseCase: UpdatePreferencesUseCase,
    private val getHiddenToolsUseCase: GetHiddenToolsUseCase,
    private val unhideToolUseCase: UnhideToolUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        launchSafe {
            combine(
                getUserPreferencesUseCase(),
                getHiddenToolsUseCase()
            ) { prefsResource, hiddenResource ->
                SettingsUiState(
                    preferences  = (prefsResource as? Resource.Success)?.data ?: UserPreferences(),
                    hiddenTools  = (hiddenResource as? Resource.Success)?.data ?: emptyList(),
                    isLoading    = prefsResource is Resource.Loading,
                    openSheet    = _uiState.value.openSheet
                )
            }.collect { _uiState.value = it }
        }
    }

    // ── Sheet management ──────────────────────────────────────────────────────
    fun openSheet(sheet: SettingsSheet) = _uiState.update { it.copy(openSheet = sheet) }
    fun closeSheet() = _uiState.update { it.copy(openSheet = null) }

    // ── Preference updates — each delegates to UpdatePreferencesUseCase ───────
    fun update(change: PreferenceUpdate) = launchSafe {
        updatePreferencesUseCase(change)
    }

    // ── Hidden tools ──────────────────────────────────────────────────────────
    fun onUnhideTool(toolId: String) = launchSafe {
        unhideToolUseCase(UnhideToolParams(toolId))
    }

    override fun handleException(throwable: Throwable) {
        _uiState.update { it.copy(isLoading = false) }
    }
}
