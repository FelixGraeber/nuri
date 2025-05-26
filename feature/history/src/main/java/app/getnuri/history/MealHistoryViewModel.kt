package app.getnuri.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.getnuri.data.IngredientDao
import app.getnuri.data.MealDao
import app.getnuri.data.UserFeedbackDao
import app.getnuri.history.model.MealWithDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MealHistoryViewModel @Inject constructor(
    private val mealDao: MealDao,
    private val userFeedbackDao: UserFeedbackDao,
    private val ingredientDao: IngredientDao
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val mealHistory: StateFlow<List<MealWithDetails>> =
        mealDao.getAllMeals()
            .flatMapLatest { meals ->
                if (meals.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val detailFlows = meals.map { meal ->
                        combine(
                            ingredientDao.getIngredientsForMealFlow(meal.id),
                            userFeedbackDao.getFeedbackForMeal(meal.id)
                        ) { ingredients, feedbackList ->
                            MealWithDetails(meal, ingredients, feedbackList)
                        }
                    }
                    combine(detailFlows) { arrayOfMealWithDetails ->
                        arrayOfMealWithDetails.toList()
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
