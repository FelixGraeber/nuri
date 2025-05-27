package com.nuri.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey
    val id: String,
    val timestamp: Long, // Instant as epoch millis
    val imagePath: String,
    val ingredientsJson: String // JSON serialized List<Ingredient>
) 