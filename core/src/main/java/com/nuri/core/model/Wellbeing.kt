package com.nuri.core.model

import java.time.Instant

data class Wellbeing(
    val id: String,
    val timestamp: Instant,
    val moodScore: Int, // 1-10 scale
    val energyLevel: Int, // 1-10 scale
    val note: String? = null
) 