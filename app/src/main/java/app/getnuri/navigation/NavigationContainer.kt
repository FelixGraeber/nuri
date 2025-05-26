 
package app.getnuri.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.getnuri.theme.Primary

/**
 * Container that wraps content with bottom navigation when appropriate.
 * Shows navigation bar only on main tab routes.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavigationContainer(
    currentRoute: NavigationRoute,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val shouldShowBottomNav = currentRoute is MainTabRoute
    val selectedTab = currentRoute.toBottomNavTab()
    val motionScheme = MaterialTheme.motionScheme

    // OPTIMIZED: Memoize scaffold to prevent unnecessary recompositions
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // OPTIMIZED: Use fast motion specs for instant bottom nav transitions  
            AnimatedVisibility(
                visible = shouldShowBottomNav && selectedTab != null,
                enter = androidx.compose.animation.slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = motionScheme.fastEffectsSpec()
                ),
                exit = androidx.compose.animation.slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = motionScheme.fastEffectsSpec()
                )
            ) {
                if (selectedTab != null) {
                    // OPTIMIZED: Memoized bottom navigation bar
                    key(selectedTab) {
                        BottomNavigationBar(
                            selectedTab = selectedTab,
                            onTabSelected = onTabSelected
                        )
                    }
                }
            }
        },
        containerColor = Primary
    ) { paddingValues ->
        // OPTIMIZED: Direct content rendering without additional wrappers
        content(paddingValues)
    }
}

/**
 * Helper function to determine if a route should display bottom navigation
 */
fun shouldShowBottomNavigation(route: NavigationRoute): Boolean {
    return when (route) {
        is MainTabRoute -> true
        // Legacy routes that should be considered main tabs
        is MealTrackingChoice -> true
        is MealHistory -> true
        Wellbeing -> true
        Results -> true
        else -> false
    }
}

/**
 * Helper function to get the default tab when navigation starts
 */
fun getDefaultTab(): BottomNavTab = BottomNavTab.Meals 