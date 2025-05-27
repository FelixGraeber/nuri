package com.nuri.core.repository

import com.nuri.core.model.Wellbeing
import kotlinx.coroutines.flow.Flow

interface WellbeingRepository {
    fun observeWellbeing(): Flow<List<Wellbeing>>
    suspend fun getAllWellbeing(): List<Wellbeing>
    suspend fun saveWellbeing(wellbeing: Wellbeing)
    suspend fun deleteWellbeing(wellbeingId: String)
} 