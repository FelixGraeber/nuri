package com.nuri.core.network

import com.nuri.core.model.Ingredient
import android.net.Uri

interface IngredientExtractor {
    suspend fun extractIngredients(imageUri: Uri): Result<List<Ingredient>>
} 