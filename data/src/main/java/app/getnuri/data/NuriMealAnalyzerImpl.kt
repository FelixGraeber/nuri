package app.getnuri.data

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import app.getnuri.RemoteConfigDataSource
import app.getnuri.data.model.MealAnalysisData
import app.getnuri.util.LocalFileProvider
import app.getnuri.vertexai.FirebaseAiDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NuriMealAnalyzerImpl @Inject constructor(
    private val firebaseAiDataSource: FirebaseAiDataSource,
    private val remoteConfigDataSource: RemoteConfigDataSource,
    private val localFileProvider: LocalFileProvider,
    @ApplicationContext private val context: Context
) : NuriMealAnalyzer {

    override suspend fun analyzeMealFromText(description: String): Result<MealAnalysisData> {
        return withContext(Dispatchers.IO) {
            try {
                // Use the remote config prompt for meal analysis
                val mealAnalysisPrompt = remoteConfigDataSource.promptMealAnalysis()
                val fullPrompt = """
                $mealAnalysisPrompt
                
                Meal Description: "$description"
                
                Please provide the analysis in the following EXACT format:
                
                Ingredients:
                Ingredient Name 1 | Quantity Unit
                Ingredient Name 2 | Quantity Unit
                Ingredient Name 3 | Quantity Unit
                
                Guidelines:
                - Use realistic portion sizes for a single serving
                - Use common units: g (grams), ml (milliliters), pieces, tbsp, tsp
                - Format each ingredient as "Name | Quantity Unit" on separate lines. If quantity or unit is unknown, it can be omitted, e.g., "Ingredient Name | " or just "Ingredient Name".
                
                Provide ONLY the Ingredients section as shown above.
                """

                // Call FirebaseAiDataSource with nutrition-focused prompt
                val aiResponse = firebaseAiDataSource.generateNutritionPrompt(fullPrompt)
                val responseText = aiResponse.generatedPrompts?.firstOrNull()

                if (responseText.isNullOrBlank()) {
                    Result.failure(Exception("AI response was empty or null for text analysis."))
                } else {
                    // Parse the responseText
                    val ingredients = parseIngredientsList(responseText)
                    Result.success(MealAnalysisData(ingredients))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed during text analysis: ${e.message}", e))
            }
        }
    }

    override suspend fun analyzeMealFromImage(imageUri: Uri): Result<MealAnalysisData> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Convert Uri to File, then to Bitmap
                val imageFile: File? = try {
                    if (imageUri.scheme == "file") {
                        imageUri.path?.let { File(it) }
                    } else {
                        // If it's a content URI, copy to a temporary cache file
                        val tempFile = localFileProvider.createCacheFile("temp_image_for_analysis.jpg")
                        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                            tempFile.outputStream().use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        tempFile
                    }
                } catch (e: Exception) {
                    return@withContext Result.failure(Exception("Failed to access image file from URI: ${e.message}", e))
                }

                if (imageFile == null || !imageFile.exists()) {
                     return@withContext Result.failure(Exception("Image file not found or not accessible from URI."))
                }
                
                val bitmap: Bitmap? = BitmapFactory.decodeFile(imageFile.absolutePath)
                if (bitmap == null) {
                    // Attempt to delete the temp file if it was created by localFileProvider
                    if (imageUri.scheme != "file" && imageFile.name.startsWith("temp_image_for_analysis")) {
                        imageFile.delete()
                    }
                    return@withContext Result.failure(Exception("Failed to decode bitmap from image file."))
                }

                // 2. First validate the meal photo using nutrition-focused validation
                val validationResult = firebaseAiDataSource.validateMealPhoto(bitmap)
                if (!validationResult.success) {
                    // Clean up temp file
                    if (imageUri.scheme != "file" && imageFile.name.startsWith("temp_image_for_analysis")) {
                        imageFile.delete()
                    }
                    return@withContext Result.failure(Exception("Image validation failed: ${validationResult.errorMessage?.description ?: "Invalid meal photo"}"))
                }

                // 3. Get meal analysis from AI using nutrition-focused prompts
                val imageAnalysisResponse = firebaseAiDataSource.analyzeMealFromImage(bitmap)
                val description = imageAnalysisResponse.userDescription 

                // Clean up the temp file if it was created
                if (imageUri.scheme != "file" && imageFile.name.startsWith("temp_image_for_analysis")) {
                    imageFile.delete()
                }
                
                if (!imageAnalysisResponse.success || description.isNullOrBlank()) {
                    return@withContext Result.failure(Exception("Failed to analyze meal from image. AI indicated failure or empty description."))
                }

                // 4. Process the AI description using meal analysis logic
                analyzeMealFromAnalysisResponse(description)
            } catch (e: Exception) {
                Result.failure(Exception("Failed during image analysis: ${e.message}", e))
            }
        }
    }

    private suspend fun analyzeMealFromAnalysisResponse(aiDescription: String): Result<MealAnalysisData> {
        return withContext(Dispatchers.IO) {
            try {
                // Use nutrition estimation prompt to get structured data
                val nutritionPrompt = remoteConfigDataSource.promptNutritionEstimation()
                val fullPrompt = """
                $nutritionPrompt
                
                AI Analysis: "$aiDescription"
                
                Convert this analysis into structured ingredient data:
                
                Ingredients:
                [List ingredients with quantities and units, e.g., "Chicken Breast | 100 g", "Olive Oil | 1 tbsp"]
                
                Provide ONLY the Ingredients section.
                """

                val aiResponse = firebaseAiDataSource.generateNutritionPrompt(fullPrompt)
                val responseText = aiResponse.generatedPrompts?.firstOrNull()

                if (responseText.isNullOrBlank()) {
                    // Fallback: parse the original AI description directly
                    Result.success(parseAiDescriptionFallback(aiDescription))
                } else {
                    val ingredients = parseIngredientsList(responseText)
                    Result.success(MealAnalysisData(ingredients))
                }
            } catch (e: Exception) {
                // Fallback on error
                Result.success(parseAiDescriptionFallback(aiDescription))
            }
        }
    }

    private fun parseAiDescriptionFallback(description: String): MealAnalysisData {
        // Simple fallback parsing: extract potential item names
        val ingredients = description.split(Regex("[,.]|\\band\\b"))
            .map { it.trim() }
            .filter { it.isNotBlank() && it.length > 2 && !it.equals("and", ignoreCase = true) }
            .take(5) // Limit to a few items for fallback
            .map { AnalyzedIngredient(name = it, quantity = null, unit = null) }
        
        return MealAnalysisData(
            extractedIngredients = ingredients.ifEmpty { 
                listOf(AnalyzedIngredient(name = "Unknown food item", quantity = null, unit = null)) 
            }
        )
    }

    private fun parseIngredientsList(text: String): List<AnalyzedIngredient> {
        val ingredientsSection = text.lines()
            .dropWhile { !it.trim().startsWith("Ingredients:", ignoreCase = true) }
            .drop(1) // Remove the "Ingredients:" line itself
            .takeWhile { it.trim().isNotEmpty() && !it.trim().startsWith("Triggers:", ignoreCase = true) } // Stop if triggers accidentally included

        return ingredientsSection
            .map { line -> line.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNull { line ->
                val parts = line.split("|", limit = 2).map { it.trim() }
                val name = parts.getOrNull(0)
                if (name.isNullOrBlank()) {
                    return@mapNotNull null // Skip if name is blank
                }

                val quantityAndUnitString = parts.getOrNull(1)
                var quantity: String? = null
                var unit: String? = null

                if (!quantityAndUnitString.isNullOrBlank()) {
                    val quantityParts = quantityAndUnitString.split(Regex("\\s+"), limit = 2)
                    quantity = quantityParts.getOrNull(0)?.takeIf { it.isNotBlank() }
                    unit = quantityParts.getOrNull(1)?.takeIf { it.isNotBlank() }
                }
                AnalyzedIngredient(name = name, quantity = quantity, unit = unit)
            }
    }
}
