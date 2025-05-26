package app.getnuri.data.model

data class AnalyzedIngredient(
    val name: String,
    val quantity: String?, // e.g., "100", "0.5", "1"
    val unit: String?      // e.g., "g", "cup", "piece"
)

data class MealAnalysisData(
    val extractedIngredients: List<AnalyzedIngredient>
)
