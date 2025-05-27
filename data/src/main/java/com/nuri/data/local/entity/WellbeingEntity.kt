package com.nuri.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "wellbeing")
data class WellbeingEntity(
    @PrimaryKey
    val id: String,
    val timestamp: Long, // Instant as epoch millis
    val moodScore: Int,
    val energyLevel: Int,
    val note: String?
) 