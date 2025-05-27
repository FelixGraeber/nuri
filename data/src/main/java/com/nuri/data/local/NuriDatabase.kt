package com.nuri.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.nuri.data.local.dao.MealDao
import com.nuri.data.local.dao.WellbeingDao
import com.nuri.data.local.entity.MealEntity
import com.nuri.data.local.entity.WellbeingEntity

@Database(
    entities = [MealEntity::class, WellbeingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NuriDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun wellbeingDao(): WellbeingDao

    companion object {
        const val DATABASE_NAME = "nuri_database"
    }
} 