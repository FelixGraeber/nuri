package com.nuri.core.repository

import com.nuri.core.model.Meal
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun observeMeals(): Flow<List<Meal>>
    suspend fun getAllMeals(): List<Meal>
    suspend fun saveMeal(meal: Meal)
    suspend fun deleteMeal(mealId: String)
} 