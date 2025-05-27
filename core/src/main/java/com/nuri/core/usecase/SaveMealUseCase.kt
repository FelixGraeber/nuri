package com.nuri.core.usecase

import com.nuri.core.model.Meal
import com.nuri.core.repository.MealRepository
import javax.inject.Inject

class SaveMealUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(meal: Meal): Result<Unit> {
        return try {
            mealRepository.saveMeal(meal)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 