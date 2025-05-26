package app.getnuri.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.getnuri.data.MealDao
import app.getnuri.data.UserFeedbackDao
import app.getnuri.history.model.MealWithFeedback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MealHistoryViewModel @Inject constructor(
    private val mealDao: MealDao,
    private val userFeedbackDao: UserFeedbackDao
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val mealHistory: StateFlow<List<MealWithFeedback>> = mealDao.getAllMeals()
        .flatMapLatest { meals ->
            if (meals.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    meals.map { meal ->
                        userFeedbackDao.getFeedbackForMeal(meal.id)
                            .map { feedbackList ->
                                MealWithFeedback(meal, feedbackList)
                            }
                    }
                ) { arrayOfMealWithFeedback ->
                    arrayOfMealWithFeedback.toList()
                }
            }
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}
