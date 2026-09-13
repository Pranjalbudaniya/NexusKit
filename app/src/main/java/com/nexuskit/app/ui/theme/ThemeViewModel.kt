package com.nexuskit.app.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexuskit.app.domain.model.UserPreferences
import com.nexuskit.app.domain.usecase.preferences.GetUserPreferencesUseCase
import com.nexuskit.app.core.base.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Provides UserPreferences to NexusKitTheme.
 * Lives at the top of the composition tree — scoped to the Activity.
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    getPreferences: GetUserPreferencesUseCase
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> =
        getPreferences()
            .map { resource ->
                when (resource) {
                    is Resource.Success -> resource.data
                    else -> UserPreferences()
                }
            }
            .stateIn(
                scope         = viewModelScope,
                started       = SharingStarted.Eagerly,
                initialValue  = UserPreferences()
            )
}
