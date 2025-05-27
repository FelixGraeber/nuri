package app.getnuri.data

import app.getnuri.RemoteConfigDataSource
import javax.inject.Singleton

@Singleton
class ConfigProvider(val remoteConfigDataSource: RemoteConfigDataSource) {

    fun isAppInactive(): Boolean {
        return remoteConfigDataSource.isAppInactive()
    }

    // Nutrition-focused methods for the Nuri app
    fun textModelName(): String = remoteConfigDataSource.textModelName()
    fun imageModelName(): String = remoteConfigDataSource.imageModelName()
    
    fun promptMealPhotoValidation(): String = remoteConfigDataSource.promptMealPhotoValidation()
    fun promptMealAnalysis(): String = remoteConfigDataSource.promptMealAnalysis()
    fun promptIngredientExtraction(): String = remoteConfigDataSource.promptIngredientExtraction()
    fun promptSymptomProcessing(): String = remoteConfigDataSource.promptSymptomProcessing()
    fun promptFoodIntoleranceDetection(): String = remoteConfigDataSource.promptFoodIntoleranceDetection()
    fun promptPatternAnalysis(): String = remoteConfigDataSource.promptPatternAnalysis()
    fun promptVoiceInputProcessing(): String = remoteConfigDataSource.promptVoiceInputProcessing()
    fun promptNutritionEstimation(): String = remoteConfigDataSource.promptNutritionEstimation()
    fun promptHealthDataCorrelation(): String = remoteConfigDataSource.promptHealthDataCorrelation()
    fun promptMealContextAnalysis(): String = remoteConfigDataSource.promptMealContextAnalysis()
    
    fun useGeminiNano(): Boolean = remoteConfigDataSource.useGeminiNano()
    fun enableAdvancedAnalytics(): Boolean = remoteConfigDataSource.enableAdvancedAnalytics()
    
    fun getNutritionTipsGifLink(): String = remoteConfigDataSource.getNutritionTipsGifLink()

    // Deprecated methods - kept for backward compatibility during transition
    @Deprecated("Use nutrition-focused methods instead")
    fun getPromoVideoLink(): String {
        return remoteConfigDataSource.getPromoVideoLink()
    }

    @Deprecated("Use getNutritionTipsGifLink() instead")
    fun getDancingDroidLink(): String {
        return remoteConfigDataSource.getDancingDroidLink()
    }
}
