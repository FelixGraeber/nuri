package com.nuri.data.di

import android.content.Context
import androidx.room.Room
import com.nuri.data.local.NuriDatabase
import com.nuri.data.local.dao.MealDao
import com.nuri.data.local.dao.WellbeingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNuriDatabase(@ApplicationContext context: Context): NuriDatabase {
        return Room.databaseBuilder(
            context,
            NuriDatabase::class.java,
            NuriDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideMealDao(database: NuriDatabase): MealDao = database.mealDao()

    @Provides
    fun provideWellbeingDao(database: NuriDatabase): WellbeingDao = database.wellbeingDao()
} 