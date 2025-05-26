package app.getnuri.feature.wellbeing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import app.getnuri.data.Meal
import app.getnuri.data.MealDao
import app.getnuri.data.UserFeedback
import app.getnuri.data.UserFeedbackDao
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

@HiltViewModel
class WellbeingViewModel @Inject constructor(
    private val mealDao: MealDao,
    private val feedbackDao: UserFeedbackDao
) : ViewModel() {

    /** Inserts feedback; invokes onComplete(true) on success, false when no recent meal found */
    fun submitFeedback(
        moodScore: Int?,
        energyScore: Int?,
        symptoms: List<String>,
        notes: String?,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val now = Instant.now().toEpochMilli()

            // Look for a meal in the last 2 h
            val recentMealId = withContext(Dispatchers.IO) {
                val recentMeal: Meal? = mealDao.getMostRecentMealBefore(now)
                val since = now - twoHoursMillis
                if (recentMeal != null && recentMeal.timestamp >= since) {
                    recentMeal.id
                } else {
                    null
                }
            }

            if (recentMealId == null) {
                // nothing to attach to
                onComplete(false)
                return@launch
            }

            val feedback = UserFeedback(
                mealId = recentMealId,
                feedbackTimestamp = now,
                feelingDescription = "",          // deprecated
                moodScore = moodScore,
                energyScore = energyScore,
                via = "scale",
                feedbackNotes = buildString {
                    append(symptoms.joinToString(", "))
                    if (!notes.isNullOrBlank()) append("\n\n$notes")
                }
            )

            withContext(Dispatchers.IO) {
                feedbackDao.insertFeedback(feedback)
            }
            onComplete(true)
        }
    }

    companion object {
        private const val twoHoursMillis = 2 * 60 * 60 * 1000L
    }
} 