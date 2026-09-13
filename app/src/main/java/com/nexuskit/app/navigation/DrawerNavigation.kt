package com.nexuskit.app.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.nexuskit.app.feature.drawer.DrawerContent
import kotlinx.coroutines.launch

/**
 * Wraps a tool screen in a left-swipe navigation drawer.
 * Every tool screen Scaffold must be a direct child of this composable.
 *
 * [currentToolId] the ID of the tool currently displayed — drives the
 *                 active row highlight inside the drawer.
 * [content]       the tool screen Scaffold.
 *
 * Navigation behaviour:
 *   • Tapping a tool in drawer → navigate to tool/{id}, remove current from stack
 *   • Tapping Home             → navigate to HomeScreen, clear back stack to home
 *   • Tapping outside drawer   → close drawer, stay on current screen
 */
@Composable
fun DrawerNavigation(
    navController: NavHostController,
    currentToolId: String?,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentToolId = currentToolId,
                onToolClick   = { toolId ->
                    scope.launch { drawerState.close() }
                    navController.navigate(NavRoutes.toolRoute(toolId)) {
                        // Replace current tool — don't grow the back stack
                        popUpTo(NavRoutes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onHomeClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    ) {
        content()
    }
}
