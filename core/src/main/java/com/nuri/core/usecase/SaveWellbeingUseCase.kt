package com.nuri.core.usecase

import com.nuri.core.model.Wellbeing
import com.nuri.core.repository.WellbeingRepository
import javax.inject.Inject

class SaveWellbeingUseCase @Inject constructor(
    private val wellbeingRepository: WellbeingRepository
) {
    suspend operator fun invoke(wellbeing: Wellbeing): Result<Unit> {
        return try {
            wellbeingRepository.saveWellbeing(wellbeing)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 