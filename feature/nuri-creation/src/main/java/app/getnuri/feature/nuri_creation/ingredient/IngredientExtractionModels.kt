package app.getnuri.feature.nuri_creation.ingredient

import app.getnuri.data.model.MealAnalysisData

data class ExtractedIngredient(
    val name: String,
    val quantity: String,
    val unit: String = "",
    val isEditable: Boolean = true
)

data class MealInfo(
    val title: String,
    val imageUri: String?,
    val scheduledTime: String? = null
)

data class IngredientExtractionUiState(
    val mealInfo: MealInfo,
    val ingredients: List<ExtractedIngredient>,
    val isLoading: Boolean = false,
    val error: String? = null
)

fun MealAnalysisData.toExtractedIngredients(): List<ExtractedIngredient> {
    return extractedIngredients.map { analyzedIngredient ->
        ExtractedIngredient(
            name = analyzedIngredient.name,
            quantity = analyzedIngredient.quantity ?: "",
            unit = analyzedIngredient.unit ?: "",
            isEditable = true // Defaulting to true, as per original class structure
        )
    }
}