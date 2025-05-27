package com.nuri.core.network

import com.nuri.core.model.Ingredient
import android.net.Uri
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeIngredientExtractor @Inject constructor() : IngredientExtractor {
    
    private val sampleIngredients = listOf(
        listOf(
            Ingredient("Tomato", "2 medium", 0.95f),
            Ingredient("Onion", "1 large", 0.90f),
            Ingredient("Garlic", "3 cloves", 0.85f)
        ),
        listOf(
            Ingredient("Chicken breast", "200g", 0.92f),
            Ingredient("Rice", "1 cup", 0.88f),
            Ingredient("Broccoli", "1 head", 0.90f)
        ),
        listOf(
            Ingredient("Banana", "2 pieces", 0.95f),
            Ingredient("Oats", "1/2 cup", 0.87f),
            Ingredient("Milk", "1 cup", 0.93f)
        )
    )

    override suspend fun extractIngredients(imageUri: Uri): Result<List<Ingredient>> {
        // Simulate network delay
        delay(2000)
        
        // Return random sample ingredients
        val randomIngredients = sampleIngredients.random()
        return Result.success(randomIngredients)
    }
} 