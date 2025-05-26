 
@file:OptIn(ExperimentalSerializationApi::class)

package app.getnuri.navigation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

interface NavigationRoute

// Remove Home route
// @Serializable
// data object Home : NavigationRoute

@Serializable
data class Create(val fileName: String? = null, val prompt: String? = null) : NavigationRoute

@Serializable
object Camera : NavigationRoute

@Serializable
object About : NavigationRoute

// New Nuri meal tracking routes
@Serializable
data class MealTrackingChoice(val fileName: String? = null) : NavigationRoute

@Serializable
object MealPhotoCapture : NavigationRoute

@Serializable
data class MealPhotoConfirmation(val imageUri: String) : NavigationRoute

@Serializable
object MealTextEntry : NavigationRoute

@Serializable
object MealHistory : NavigationRoute

@Serializable
data class FeedbackEntry(val mealId: Long) : NavigationRoute

@Serializable
data class IngredientExtraction(
    val mealTitle: String = "Avocado Toast with Poached Eggs",
    val mealImageUri: String? = null,
    val extractedIngredients: List<String> = listOf(
        "Sourdough Bread | 180g",
        "Avocado | 120g", 
        "Poached Eggs | 100g",
        "Cherry Tomatoes | 120g",
        "Salt | 1g",
        "Pepper | 1g"
    ),
    val potentialTriggers: List<String> = listOf("Gluten", "Eggs"),
    val extractedQuantities: List<String> = emptyList()
) : NavigationRoute

// Bottom Navigation Routes
@Serializable
object Wellbeing : NavigationRoute

@Serializable
object Results : NavigationRoute

// Interface to define main tab routes (routes that should show bottom navigation)
interface MainTabRoute : NavigationRoute

// Make main tab routes implement MainTabRoute interface
@Serializable
data class MealTrackingChoiceTab(val fileName: String? = null) : NavigationRoute, MainTabRoute

@Serializable  
object WellbeingTab : NavigationRoute, MainTabRoute

@Serializable
object MealHistoryTab : NavigationRoute, MainTabRoute

@Serializable
object ResultsTab : NavigationRoute, MainTabRoute
