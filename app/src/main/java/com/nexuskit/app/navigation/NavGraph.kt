package com.nexuskit.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.feature.home.HomeScreen
import com.nexuskit.app.feature.onboarding.OnboardingScreen
import com.nexuskit.app.feature.onboarding.SplashScreen
import com.nexuskit.app.feature.settings.SettingsScreen
import com.nexuskit.app.feature.tools.base_converter.BaseConverterScreen
import com.nexuskit.app.feature.tools.bmi_calculator.BmiScreen
import com.nexuskit.app.feature.tools.calculator.CalculatorScreen
import com.nexuskit.app.feature.tools.case_converter.CaseConverterScreen
import com.nexuskit.app.feature.tools.color_palette.ColorPaletteScreen
import com.nexuskit.app.feature.tools.date_calculator.DateCalcScreen
import com.nexuskit.app.feature.tools.developer_tools.DevToolsScreen
import com.nexuskit.app.feature.tools.discount_calculator.DiscountScreen
import com.nexuskit.app.feature.tools.hash_generator.HashScreen
import com.nexuskit.app.feature.tools.health_fitness.FitnessScreen
import com.nexuskit.app.feature.tools.loan_calculator.LoanScreen
import com.nexuskit.app.feature.tools.password_generator.PasswordGeneratorScreen
import com.nexuskit.app.feature.tools.percentage_calc.PercentageScreen
import com.nexuskit.app.feature.tools.productivity_suite.ProductivityScreen
import com.nexuskit.app.feature.tools.qr_generator.QrGeneratorScreen
import com.nexuskit.app.feature.tools.reference_sheets.ReferenceScreen
import com.nexuskit.app.feature.tools.science_education.ScienceScreen
import com.nexuskit.app.feature.tools.screen_light.ScreenLightScreen
import com.nexuskit.app.feature.tools.stopwatch.StopwatchScreen
import com.nexuskit.app.feature.tools.text_editor.TextEditorScreen
import com.nexuskit.app.feature.tools.text_suite.TextSuiteScreen
import com.nexuskit.app.feature.tools.encryption.EncryptionScreen
import com.nexuskit.app.feature.tools.sip_calculator.SipScreen
import com.nexuskit.app.feature.tools.unit_converter.UnitConverterScreen
import com.nexuskit.app.feature.tools.world_clock.WorldClockScreen

/**
 * Root navigation host for the entire app.
 * Called directly from MainActivity.
 *
 * Transition spec:
 *   Home → Tool     : slide in from right (ANIM_DURATION ms)
 *   Tool → Home     : slide out to right
 *   Any  → Settings : slide in from right
 *   Splash/Onboarding → Home : fade only (no slide — feels like a launch, not a push)
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    initialToolRoute: String? = null
) {
    NavHost(
        navController    = navController,
        startDestination = NavRoutes.SPLASH,
        enterTransition  = { defaultEnterTransition() },
        exitTransition   = { defaultExitTransition() },
        popEnterTransition  = { defaultPopEnterTransition() },
        popExitTransition   = { defaultPopExitTransition() }
    ) {

        // ── Splash ────────────────────────────────────────────────────────────
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(NavRoutes.ONBOARDING) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    val dest = if (!initialToolRoute.isNullOrBlank()) initialToolRoute else NavRoutes.HOME
                    navController.navigate(dest) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Onboarding ────────────────────────────────────────────────────────
        composable(
            route = NavRoutes.ONBOARDING,
            enterTransition  = { fadeIn(tween(AppConstants.ANIMATION_DURATION_MS)) },
            exitTransition   = { fadeOut(tween(AppConstants.ANIMATION_DURATION_MS)) }
        ) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ──────────────────────────────────────────────────────────────
        composable(
            route = NavRoutes.HOME,
            enterTransition  = { fadeIn(tween(AppConstants.ANIMATION_DURATION_MS)) },
            exitTransition   = { fadeOut(tween(AppConstants.ANIMATION_DURATION_MS)) },
            popEnterTransition  = { fadeIn(tween(AppConstants.ANIMATION_DURATION_MS)) },
            popExitTransition   = { fadeOut(tween(AppConstants.ANIMATION_DURATION_MS)) }
        ) {
            HomeScreen(
                onToolClick = { toolId ->
                    navController.navigate(NavRoutes.toolRoute(toolId))
                },
                onSettingsClick = {
                    navController.navigate(NavRoutes.SETTINGS)
                }
            )
        }

        // ── Settings ──────────────────────────────────────────────────────────
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Tool Screens ──────────────────────────────────────────────────────

        composable(NavRoutes.TOOL_CALCULATOR) {
            CalculatorScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_PERCENTAGE_CALC) {
            PercentageScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_BASE_CONVERTER) {
            BaseConverterScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_UNIT_CONVERTER) {
            UnitConverterScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_QR_GENERATOR) {
            QrGeneratorScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_PASSWORD_GENERATOR) {
            PasswordGeneratorScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_TEXT_EDITOR) {
            TextEditorScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_STOPWATCH) {
            StopwatchScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_BMI_CALCULATOR) {
            BmiScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_DATE_CALCULATOR) {
            DateCalcScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_DISCOUNT_CALCULATOR) {
            DiscountScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_LOAN_CALCULATOR) {
            LoanScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_CASE_CONVERTER) {
            CaseConverterScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_HASH_GENERATOR) {
            HashScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_COLOR_PALETTE) {
            ColorPaletteScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_WORLD_CLOCK) {
            WorldClockScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_SCREEN_LIGHT) {
            ScreenLightScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_DEVELOPER_TOOLS) {
            DevToolsScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_TEXT_SUITE) {
            TextSuiteScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_SCIENCE_EDUCATION) {
            ScienceScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_HEALTH_FITNESS) {
            FitnessScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_PRODUCTIVITY_SUITE) {
            ProductivityScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_REFERENCE_SHEETS) {
            ReferenceScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_SIP_CALCULATOR) {
            SipScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.TOOL_ENCRYPTION) {
            EncryptionScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// ── Transition helpers ─────────────────────────────────────────────────────────

private const val SLIDE_DURATION = AppConstants.ANIMATION_DURATION_MS

private fun AnimatedContentTransitionScope<*>.defaultEnterTransition(): EnterTransition =
    slideInHorizontally(tween(SLIDE_DURATION)) { it }

private fun AnimatedContentTransitionScope<*>.defaultExitTransition(): ExitTransition =
    slideOutHorizontally(tween(SLIDE_DURATION)) { -it }

private fun AnimatedContentTransitionScope<*>.defaultPopEnterTransition(): EnterTransition =
    slideInHorizontally(tween(SLIDE_DURATION)) { -it }

private fun AnimatedContentTransitionScope<*>.defaultPopExitTransition(): ExitTransition =
    slideOutHorizontally(tween(SLIDE_DURATION)) { it }
