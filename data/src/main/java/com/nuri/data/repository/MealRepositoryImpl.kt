package com.nuri.core.repository

import com.nuri.core.model.Meal
import com.nuri.core.model.Ingredient
import com.nuri.data.local.dao.MealDao
import com.nuri.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.Instant
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val json: Json
) : MealRepository {

    override fun observeMeals(): Flow<List<Meal>> {
        return mealDao.observeAllMeals().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getAllMeals(): List<Meal> {
        return mealDao.getAllMeals().map { it.toDomainModel() }
    }

    override suspend fun saveMeal(meal: Meal) {
        mealDao.insertMeal(meal.toEntity())
    }

    override suspend fun deleteMeal(mealId: String) {
        mealDao.deleteMealById(mealId)
    }

    private fun MealEntity.toDomainModel(): Meal {
        val ingredients = json.decodeFromString<List<Ingredient>>(ingredientsJson)
        return Meal(
            id = id,
            timestamp = Instant.ofEpochMilli(timestamp),
            imagePath = imagePath,
            ingredients = ingredients
        )
    }

    private fun Meal.toEntity(): MealEntity {
        return MealEntity(
            id = id,
            timestamp = timestamp.toEpochMilli(),
            imagePath = imagePath,
            ingredientsJson = json.encodeToString(ingredients)
        )
    }
} 