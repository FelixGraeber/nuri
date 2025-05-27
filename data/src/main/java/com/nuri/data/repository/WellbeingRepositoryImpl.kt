package com.nuri.core.repository

import com.nuri.core.model.Wellbeing
import com.nuri.data.local.dao.WellbeingDao
import com.nuri.data.local.entity.WellbeingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class WellbeingRepositoryImpl @Inject constructor(
    private val wellbeingDao: WellbeingDao
) : WellbeingRepository {

    override fun observeWellbeing(): Flow<List<Wellbeing>> {
        return wellbeingDao.observeAllWellbeing().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getAllWellbeing(): List<Wellbeing> {
        return wellbeingDao.getAllWellbeing().map { it.toDomainModel() }
    }

    override suspend fun saveWellbeing(wellbeing: Wellbeing) {
        wellbeingDao.insertWellbeing(wellbeing.toEntity())
    }

    override suspend fun deleteWellbeing(wellbeingId: String) {
        wellbeingDao.deleteWellbeingById(wellbeingId)
    }

    private fun WellbeingEntity.toDomainModel(): Wellbeing {
        return Wellbeing(
            id = id,
            timestamp = Instant.ofEpochMilli(timestamp),
            moodScore = moodScore,
            energyLevel = energyLevel,
            note = note
        )
    }

    private fun Wellbeing.toEntity(): WellbeingEntity {
        return WellbeingEntity(
            id = id,
            timestamp = timestamp.toEpochMilli(),
            moodScore = moodScore,
            energyLevel = energyLevel,
            note = note
        )
    }
} 