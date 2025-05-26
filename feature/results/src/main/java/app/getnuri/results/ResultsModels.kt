package app.getnuri.results

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Stable
import java.time.LocalDate
import java.time.LocalDateTime

@Stable
data class ResultState(
    val resultImageBitmap: Bitmap? = null,
    val originalImageUrl: String? = null,
    val promptText: String? = null,
    val savedUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val wellbeingData: WellbeingData = WellbeingData()
)

@Stable
data class WellbeingData(
    val moodEntries: List<MoodEntry> = emptyList(),
    val energyEntries: List<EnergyEntry> = emptyList(),
    val symptomEntries: List<SymptomEntry> = emptyList(),
    val insights: List<WellbeingInsight> = emptyList()
)

@Stable
data class MoodEntry(
    val date: LocalDate,
    val mood: Float, // 1-10 scale
    val notes: String = ""
)

@Stable
data class EnergyEntry(
    val date: LocalDate,
    val timeOfDay: String, // "Morning", "Afternoon", "Evening"
    val energyLevel: Float // 1-10 scale
)

@Stable
data class SymptomEntry(
    val date: LocalDate,
    val symptom: String,
    val severity: Float, // 1-10 scale
    val timestamp: LocalDateTime
)

@Stable
data class WellbeingInsight(
    val title: String,
    val description: String,
    val type: InsightType,
    val confidence: Float // 0-1 scale
)

enum class InsightType {
    MOOD_PATTERN,
    ENERGY_PATTERN,
    SYMPTOM_CORRELATION,
    GENERAL_TREND
}

enum class ResultOption {
    WellbeingCharts,
    NutritionInsights,
    FoodTriggers
}

// Mock data generator for previews
object MockWellbeingDataGenerator {
    fun generateMockData(): WellbeingData {
        val today = LocalDate.now()
        
        val moodEntries = (0..6).map { dayOffset ->
            MoodEntry(
                date = today.minusDays(dayOffset.toLong()),
                mood = (6..9).random().toFloat(),
                notes = "Feeling good today"
            )
        }
        
        val energyEntries = (0..6).flatMap { dayOffset ->
            listOf("Morning", "Afternoon", "Evening").map { timeOfDay ->
                EnergyEntry(
                    date = today.minusDays(dayOffset.toLong()),
                    timeOfDay = timeOfDay,
                    energyLevel = (5..9).random().toFloat()
                )
            }
        }
        
        val symptomEntries = (0..6).mapNotNull { dayOffset ->
            if ((0..2).random() == 0) { // 33% chance of symptom
                SymptomEntry(
                    date = today.minusDays(dayOffset.toLong()),
                    symptom = listOf("Headache", "Fatigue", "Bloating", "Nausea").random(),
                    severity = (2..6).random().toFloat(),
                    timestamp = today.minusDays(dayOffset.toLong()).atTime(12, 0)
                )
            } else null
        }
        
        val insights = listOf(
            WellbeingInsight(
                title = "Mood Improvement",
                description = "Your mood has been consistently improving over the past week",
                type = InsightType.MOOD_PATTERN,
                confidence = 0.85f
            ),
            WellbeingInsight(
                title = "Energy Dip Pattern",
                description = "You tend to have lower energy in the afternoons",
                type = InsightType.ENERGY_PATTERN,
                confidence = 0.72f
            )
        )
        
        return WellbeingData(
            moodEntries = moodEntries,
            energyEntries = energyEntries,
            symptomEntries = symptomEntries,
            insights = insights
        )
    }
}
