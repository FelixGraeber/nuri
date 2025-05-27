package com.nuri.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nuri.feature.camera.MealCaptureScreen
import com.nuri.feature.wellbeing.WellbeingEntryScreen
import com.nuri.feature.history.HistoryScreen
import com.nuri.feature.home.HomeScreen

@Composable
fun NuriNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToCapture = {
                    navController.navigate("meal_capture")
                },
                onNavigateToHistory = {
                    navController.navigate("history")
                }
            )
        }

        composable("meal_capture") {
            MealCaptureScreen(
                onNavigateToWellbeing = {
                    navController.navigate("wellbeing_entry") {
                        popUpTo("meal_capture") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("wellbeing_entry") {
            WellbeingEntryScreen(
                onNavigateToHistory = {
                    navController.navigate("history") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("history") {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 