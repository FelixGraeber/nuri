package com.nuri.core.model

import java.time.Instant

data class Meal(
    val id: String,
    val timestamp: Instant,
    val imagePath: String,
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val name: String,
    val quantity: String? = null,
    val confidence: Float = 1.0f
) 