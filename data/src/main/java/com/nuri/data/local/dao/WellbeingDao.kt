package com.nuri.data.local.dao

import androidx.room.*
import com.nuri.data.local.entity.WellbeingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WellbeingDao {
    @Query("SELECT * FROM wellbeing ORDER BY timestamp DESC")
    fun observeAllWellbeing(): Flow<List<WellbeingEntity>>

    @Query("SELECT * FROM wellbeing ORDER BY timestamp DESC")
    suspend fun getAllWellbeing(): List<WellbeingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWellbeing(wellbeing: WellbeingEntity)

    @Delete
    suspend fun deleteWellbeing(wellbeing: WellbeingEntity)

    @Query("DELETE FROM wellbeing WHERE id = :wellbeingId")
    suspend fun deleteWellbeingById(wellbeingId: String)
} 