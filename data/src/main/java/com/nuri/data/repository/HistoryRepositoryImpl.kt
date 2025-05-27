package com.nuri.core.repository

import com.nuri.core.model.HistoryItem
import com.nuri.core.model.MealHistoryItem
import com.nuri.core.model.WellbeingHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val mealRepository: MealRepository,
    private val wellbeingRepository: WellbeingRepository
) : HistoryRepository {

    override fun getHistory(): Flow<List<HistoryItem>> {
        return combine(
            mealRepository.observeMeals(),
            wellbeingRepository.observeWellbeing()
        ) { meals, wellbeingEntries ->
            val historyItems = mutableListOf<HistoryItem>()
            
            // Add meal items
            historyItems.addAll(meals.map { MealHistoryItem(it) })
            
            // Add wellbeing items
            historyItems.addAll(wellbeingEntries.map { WellbeingHistoryItem(it) })
            
            // Sort by timestamp descending (most recent first)
            historyItems.sortedByDescending { it.timestamp }
        }
    }
} 