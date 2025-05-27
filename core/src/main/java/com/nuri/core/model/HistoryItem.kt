package com.nuri.core.model

import java.time.Instant

sealed interface HistoryItem {
    val timestamp: Instant
    val id: String
}

data class MealHistoryItem(
    val meal: Meal
) : HistoryItem {
    override val timestamp: Instant = meal.timestamp
    override val id: String = meal.id
}

data class WellbeingHistoryItem(
    val wellbeing: Wellbeing
) : HistoryItem {
    override val timestamp: Instant = wellbeing.timestamp
    override val id: String = wellbeing.id
} 