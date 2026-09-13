package com.nexuskit.app.feature.onboarding

import com.nexuskit.app.core.base.BaseViewModel
import com.nexuskit.app.domain.model.PreferenceUpdate
import com.nexuskit.app.domain.usecase.preferences.UpdatePreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val updatePreferencesUseCase: UpdatePreferencesUseCase
) : BaseViewModel() {

    /**
     * Fire-and-forget — persists the onboarding-complete flag.
     * The screen navigates immediately on tap without waiting for this
     * write to finish; DataStore writes are fast and the flag only
     * matters on the NEXT app launch (SplashViewModel reads it then).
     */
    fun onGetStarted() = launchSafe {
        updatePreferencesUseCase(PreferenceUpdate.CompleteOnboarding)
    }

    override fun handleException(throwable: Throwable) {
        // Non-critical — if this write fails, onboarding simply shows
        // again on next launch. Never blocks navigation.
    }
}
