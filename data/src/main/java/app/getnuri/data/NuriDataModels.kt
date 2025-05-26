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
    val rawExtractedIngredients: List<String>,
    val rawExtractedTriggers: List<String>,
    val userConfirmedIngredients: List<String>,
    val userConfirmedTriggers: List<String>,
    val notes: String? = null,
    val recognizedIngredients: List<Ingredient> = emptyList(),
    val lat: Double? = null,
    val lon: Double? = null,
    val processingState: String = "pending",
    val opticalModelVer: String? = null
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

    @Deprecated("Replaced by moodScore; kept for graceful migration")
    val feelingDescription: String,

    val moodScore: Int? = null,   // 1..5
    val energyScore: Int? = null, // 1..5
    val via: String? = null,      // "voice" | "chat" | "scale"

    val customFeeling: String? = null,
    val feedbackNotes: String? = null
)

data class Ingredient(
    val name: String,
    val quantity: Double? = null,
    val unit: String? = null
)

data class SymptomDetail(
    val name: String,
    val intensity: Int // 1..5
)
