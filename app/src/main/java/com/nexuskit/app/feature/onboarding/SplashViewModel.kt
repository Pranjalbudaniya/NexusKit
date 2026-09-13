package com.nexuskit.app.feature.onboarding

import com.nexuskit.app.core.base.BaseViewModel
import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.usecase.preferences.GetUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Where the splash screen should navigate once its timer completes. */
enum class SplashDestination { HOME, ONBOARDING }

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase
) : BaseViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    /** null while splash timer is running. Non-null once it's safe to navigate. */
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        launchSafe {
            delay(AppConstants.SPLASH_DURATION_MS)

            val prefsResource = getUserPreferencesUseCase().first()
            val onboardingCompleted = (prefsResource as? Resource.Success)
                ?.data
                ?.onboardingCompleted
                ?: false

            _destination.value = if (onboardingCompleted) {
                SplashDestination.HOME
            } else {
                SplashDestination.ONBOARDING
            }
        }
    }

    override fun handleException(throwable: Throwable) {
        // On any read failure, default to Onboarding — never strand the user
        // on the splash screen.
        _destination.value = SplashDestination.ONBOARDING
    }
}
