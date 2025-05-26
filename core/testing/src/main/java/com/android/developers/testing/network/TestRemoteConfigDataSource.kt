 
package com.android.developers.testing.network

import app.getnuri.RemoteConfigDataSource

class TestRemoteConfigDataSource(private val useGeminiNano: Boolean) : RemoteConfigDataSource {
    override fun isAppInactive(): Boolean {
        return false
    }

    override fun textModelName(): String {
        return "gemini-1.5-flash"
    }

    override fun imageModelName(): String {
        return "gemini-1.5-flash"
    }

    override fun promptMealPhotoValidation(): String {
        return "Analyze this meal photo for nutritional content"
    }

    override fun promptMealAnalysis(): String {
        return "Provide detailed nutritional analysis of this meal"
    }

    override fun promptIngredientExtraction(): String {
        return "Extract ingredients and portions from this meal"
    }

    override fun promptSymptomProcessing(): String {
        return "Process symptoms and wellness feedback"
    }

    override fun promptFoodIntoleranceDetection(): String {
        return "Detect potential food intolerances and triggers"
    }

    override fun promptPatternAnalysis(): String {
        return "Analyze eating patterns and health correlations"
    }

    override fun promptVoiceInputProcessing(): String {
        return "Process voice input for meal logging"
    }

    override fun promptNutritionEstimation(): String {
        return "Estimate nutritional values for this meal"
    }

    override fun promptHealthDataCorrelation(): String {
        return "Correlate meal data with health metrics"
    }

    override fun promptMealContextAnalysis(): String {
        return "Analyze meal context and timing"
    }

    override fun enableAdvancedAnalytics(): Boolean {
        return true
    }

    override fun getNutritionTipsGifLink(): String {
        return "https://example.com/nutrition-tips.gif"
    }

    override fun promptTextVerify(): String {
        return "Verify text input for meal logging"
    }

    override fun promptImageValidation(): String {
        return "Validate meal image quality"
    }

    override fun promptImageDescription(): String {
        return "Describe meal image contents"
    }

    override fun useGeminiNano(): Boolean {
        return useGeminiNano
    }

    // generateBotPrompt() was removed
    // promptImageGenerationWithSkinTone() was removed
    
    override fun getPromoVideoLink(): String {
        return "https://example.com/promo-video.mp4"
    }
    
    // getDancingDroidLink() was removed
}
