package com.nuri.core.usecase

import com.nuri.core.model.Ingredient
import com.nuri.core.network.IngredientExtractor
import android.net.Uri
import javax.inject.Inject

class ExtractIngredientsUseCase @Inject constructor(
    private val ingredientExtractor: IngredientExtractor
) {
    suspend operator fun invoke(imageUri: Uri): Result<List<Ingredient>> {
        return ingredientExtractor.extractIngredients(imageUri)
    }
} 