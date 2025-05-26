package app.getnuri.history.model

import app.getnuri.data.Ingredient
import app.getnuri.data.Meal
import app.getnuri.data.UserFeedback

data class MealWithDetails(
    val meal: Meal,
    val ingredients: List<Ingredient>,
    val feedback: List<UserFeedback>
    // We can add symptoms and healthData here later
)
