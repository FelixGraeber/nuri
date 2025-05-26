package app.getnuri.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long

    @Update
    suspend fun updateMeal(meal: Meal)

    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE id = :mealId")
    fun getMealById(mealId: Long): Flow<Meal?>
}

@Dao
interface UserFeedbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: UserFeedback): Long

    @Query("SELECT * FROM user_feedback WHERE mealId = :mealId ORDER BY feedbackTimestamp DESC")
    fun getFeedbackForMeal(mealId: Long): Flow<List<UserFeedback>>
    
    @Query("SELECT * FROM user_feedback ORDER BY feedbackTimestamp DESC")
    fun getAllFeedback(): Flow<List<UserFeedback>>
}

@Dao
interface IngredientDao {
    @Insert
    suspend fun insertAll(ingredients: List<Ingredient>)

    @Query("SELECT * FROM ingredients WHERE mealId = :mealId")
    fun getIngredientsForMeal(mealId: Long): List<Ingredient> // Consider Flow<List<Ingredient>> for reactive updates

    @Query("SELECT * FROM ingredients WHERE mealId = :mealId")
    fun getIngredientsForMealFlow(mealId: Long): Flow<List<Ingredient>>

    @Update
    suspend fun update(ingredient: Ingredient)

    @Delete
    suspend fun delete(ingredient: Ingredient)

    @Query("DELETE FROM ingredients WHERE mealId = :mealId")
    suspend fun deleteIngredientsForMeal(mealId: Long)
}

@Dao
interface SymptomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(symptom: Symptom): Long

    @Query("SELECT * FROM symptoms ORDER BY timestamp DESC")
    fun getAllSymptoms(): Flow<List<Symptom>>

    @Query("SELECT * FROM symptoms WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSymptomsForUser(userId: Long): Flow<List<Symptom>>

    @Query("SELECT * FROM symptoms WHERE id = :symptomId")
    suspend fun getSymptomById(symptomId: Long): Symptom?

    @Update
    suspend fun update(symptom: Symptom)

    @Delete
    suspend fun delete(symptom: Symptom)
}

@Dao
interface HealthDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(healthData: HealthData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(healthDataList: List<HealthData>)

    @Query("SELECT * FROM health_data ORDER BY timestamp DESC")
    fun getAllHealthData(): Flow<List<HealthData>>

    @Query("SELECT * FROM health_data WHERE userId = :userId ORDER BY timestamp DESC")
    fun getHealthDataForUser(userId: Long): Flow<List<HealthData>>

    @Query("SELECT * FROM health_data WHERE type = :type ORDER BY timestamp DESC")
    fun getHealthDataByType(type: String): Flow<List<HealthData>>

    @Update
    suspend fun update(healthData: HealthData)

    @Delete
    suspend fun delete(healthData: HealthData)
}

@Dao
interface AnalysisResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: AnalysisResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<AnalysisResult>)

    @Query("SELECT * FROM analysis_results ORDER BY timestamp DESC")
    fun getAllAnalysisResults(): Flow<List<AnalysisResult>>

    @Query("SELECT * FROM analysis_results WHERE id = :id")
    suspend fun getAnalysisResultById(id: Long): AnalysisResult?

    @Update
    suspend fun update(result: AnalysisResult)

    @Query("DELETE FROM analysis_results WHERE id = :id")
    suspend fun deleteById(id: Long)
}
