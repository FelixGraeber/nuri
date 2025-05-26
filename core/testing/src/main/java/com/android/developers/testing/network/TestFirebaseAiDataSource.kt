 
package com.android.developers.testing.network

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import app.getnuri.model.GeneratedPrompt
import app.getnuri.model.ValidatedDescription
import app.getnuri.model.ValidatedImage
import app.getnuri.vertexai.FirebaseAiDataSource

class TestFirebaseAiDataSource(val promptOutput: List<String>) : FirebaseAiDataSource {
    
    // New nutrition-focused methods
    override suspend fun validateMealPhoto(image: Bitmap): ValidatedImage {
        return ValidatedImage(true, null)
    }

    override suspend fun analyzeMealFromImage(image: Bitmap): ValidatedDescription {
        return ValidatedDescription(true, "Test meal analysis: chicken breast with rice and vegetables")
    }

    override suspend fun processSymptomInput(inputPrompt: String): ValidatedDescription {
        return ValidatedDescription(true, "Processed symptoms: mild bloating after 30 minutes")
    }

    override suspend fun generateNutritionPrompt(prompt: String): GeneratedPrompt {
        return GeneratedPrompt(true, promptOutput)
    }
    
    // Legacy methods for backward compatibility
    override suspend fun validatePromptHasEnoughInformation(inputPrompt: String): ValidatedDescription {
        return processSymptomInput(inputPrompt)
    }

    override suspend fun validateImageHasEnoughInformation(image: Bitmap): ValidatedImage {
        return validateMealPhoto(image)
    }

    override suspend fun generateDescriptivePromptFromImage(image: Bitmap): ValidatedDescription {
        return analyzeMealFromImage(image)
    }

    override suspend fun generateImageFromPromptAndSkinTone(
        prompt: String,
        skinTone: String,
    ): Bitmap {
        return createBitmap(1, 1)
    }

    override suspend fun generatePrompt(prompt: String): GeneratedPrompt {
        return generateNutritionPrompt(prompt)
    }
}
