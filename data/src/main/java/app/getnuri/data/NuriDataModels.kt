package app.getnuri.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long, // milliseconds since epoch
    val inputType: String, // "PHOTO", "TEXT"
    val photoUri: String? = null,
    val description: String? = null,
    val notes: String? = null
)

@Entity(
    tableName = "user_feedback",
    foreignKeys = [
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserFeedback(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val mealId: Long,
    val feedbackTimestamp: Long,
    val feelingDescription: String, // e.g., "Good", "Bloated", "Energized"
    val customFeeling: String? = null,
    val feedbackNotes: String? = null
)

@Entity(
    tableName = "ingredients",
    foreignKeys = [
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val mealId: Long,
    val name: String,
    val quantity: Double,
    val unit: String
)

@Entity(tableName = "symptoms")
data class Symptom(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long, // Placeholder for now, no direct FK
    val timestamp: Long,
    val name: String,
    val severity: Int,
    val notes: String? = null
)

@Entity(tableName = "health_data")
data class HealthData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long, // Placeholder for now, no direct FK
    val type: String,
    val value: String,
    val unit: String,
    val timestamp: Long,
    val source: String
)

@Entity(tableName = "analysis_results")
@androidx.room.TypeConverters(NuriTypeConverters::class) // Specify here if type converters are specific to this table
data class AnalysisResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long, // When the analysis was generated
    val title: String, // Short summary of the finding
    val description: String, // Detailed explanation
    val type: String, // e.g., "Correlation", "PotentialIntolerance", "Pattern"
    val confidence: Float, // 0.0 to 1.0
    
    // Using TypeConverters for these lists
    val relatedMealIds: List<Long>? = null,
    val relatedSymptomIds: List<Long>? = null,
    val relatedHealthDataIds: List<Long>? = null,
    
    val userFeedback: String? = null, // e.g., "Helpful", "Dismissed"
    val aiModelVersion: String? = null
)
