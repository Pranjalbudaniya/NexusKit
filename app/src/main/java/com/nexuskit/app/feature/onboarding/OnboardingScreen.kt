package com.nexuskit.app.feature.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexuskit.app.R
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.ui.theme.LocalSpacing

/**
 * Single onboarding screen, shown once on first launch only.
 * Signature matches NavGraph.kt (Spec 07) exactly — do not change.
 *
 * App name is read from AppConstants.APP_NAME_DEFAULT — see header note
 * on why UserPreferences is not used here.
 */
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.xl, vertical = spacing.xxl)
        ) {
            // Push content to vertical centre
            Spacer(modifier = Modifier.weight(1f))

            // App name
            Text(
                text  = AppConstants.APP_NAME_DEFAULT,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            // Tagline
            Text(
                text  = stringResource(R.string.onboarding_headline),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(spacing.lg))

            // Description
            Text(
                text  = stringResource(R.string.onboarding_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Push button to bottom
            Spacer(modifier = Modifier.weight(1f))

            // Get Started button
            Button(
                onClick = {
                    viewModel.onGetStarted()
                    onGetStarted()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text  = stringResource(R.string.onboarding_cta),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
