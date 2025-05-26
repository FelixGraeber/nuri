/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.getnuri.data

import android.content.Context
import app.getnuri.RemoteConfigDataSource
import app.getnuri.RemoteConfigDataSourceImpl
import app.getnuri.util.LocalFileProvider
import app.getnuri.util.LocalFileProviderImpl
import app.getnuri.vertexai.FirebaseAiDataSource
import app.getnuri.vertexai.FirebaseAiDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule { // Changed to abstract class for @Binds

    @Binds
    abstract fun bindHealthConnectRepository(impl: HealthConnectRepositoryImpl): HealthConnectRepository

    // Companion object for @Provides methods if any remain, or move them to another object module
    companion object {
        @Provides
        @Singleton
        fun provideNuriMealAnalyzer(impl: NuriMealAnalyzerImpl): NuriMealAnalyzer = impl

        // NuriTypeConverters is now @ProvidedTypeConverter and injected by Hilt into NuriDatabase.
        // So, explicit provision of StringListConverter or NuriTypeConverters here for the database builder
        // is not needed if NuriDatabase correctly uses @TypeConverters(NuriTypeConverters::class)
        // and NuriTypeConverters is @ProvidedTypeConverter @Inject constructor().

        @Provides
        @Singleton
        fun provideNuriDatabase(
            @ApplicationContext context: Context,
            // stringListConverter: StringListConverter // No longer needed here
            nuriTypeConverters: NuriTypeConverters // For Room to auto-inject if needed
        ): NuriDatabase {
            return Room.databaseBuilder(context, NuriDatabase::class.java, "nuri_database.db")
                // .addTypeConverter(stringListConverter) // Removed
                .addTypeConverter(nuriTypeConverters) // Added NuriTypeConverters
                .addMigrations(NuriDatabase.MIGRATION_1_2, NuriDatabase.MIGRATION_2_3) // Added MIGRATION_2_3
                .build()
        }

        @Provides
        fun provideMealDao(database: NuriDatabase): MealDao = database.mealDao()

        @Provides
        fun provideUserFeedbackDao(database: NuriDatabase): UserFeedbackDao = database.userFeedbackDao()

        // Need to provide IngredientDao, SymptomDao, HealthDataDao as well
        @Provides
        fun provideIngredientDao(database: NuriDatabase): IngredientDao = database.ingredientDao()

        @Provides
        fun provideSymptomDao(database: NuriDatabase): SymptomDao = database.symptomDao()

        @Provides
        fun provideHealthDataDao(database: NuriDatabase): HealthDataDao = database.healthDataDao()

        @Provides
        fun provideAnalysisResultDao(database: NuriDatabase): AnalysisResultDao = database.analysisResultDao()


        @Provides
        @Named("IO")
        fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        @Singleton
        fun provideLocalFileProvider(@ApplicationContext appContext: Context): LocalFileProvider =
            LocalFileProviderImpl(appContext)

        @Provides
        @Singleton
        fun provideRemoteConfigDataSource(): RemoteConfigDataSource = RemoteConfigDataSourceImpl()

        @Provides
        fun provideConfigProvider(remoteConfigDataSource: RemoteConfigDataSource): ConfigProvider =
            ConfigProvider(remoteConfigDataSource)

        @Provides
        @Singleton
        fun providesGeminiNanoDownloader(@ApplicationContext appContext: Context): GeminiNanoDownloader =
            GeminiNanoDownloader(appContext)

        @Provides
        @Singleton
        fun providesInternetConnectivityManager(@ApplicationContext appContext: Context): InternetConnectivityManager =
            InternetConnectivityManagerImpl(appContext)

        @Provides
        @Singleton
        fun providesFirebaseVertexAiDataSource(remoteConfigDataSource: RemoteConfigDataSource): FirebaseAiDataSource =
            FirebaseAiDataSourceImpl(remoteConfigDataSource)


        @Provides
        @Singleton
        fun providesGeminiNanoDataSource(geminiNanoDownloader: GeminiNanoDownloader): GeminiNanoGenerationDataSource =
            GeminiNanoGenerationDataSourceImpl(geminiNanoDownloader)

        @Provides
        @Singleton
        fun imageGenerationRepository(
            remoteConfigDataSource: RemoteConfigDataSource,
            localFileProvider: LocalFileProvider,
            internetConnectivityManager: InternetConnectivityManager,
            firebaseAiDataSource: FirebaseAiDataSource,
            geminiNanoGenerationDataSource: GeminiNanoGenerationDataSource,
        ): ImageGenerationRepository = ImageGenerationRepositoryImpl(
            remoteConfigDataSource = remoteConfigDataSource,
            localFileProvider = localFileProvider,
            geminiNanoDataSource = geminiNanoGenerationDataSource,
            internetConnectivityManager = internetConnectivityManager,
            firebaseAiDataSource = firebaseAiDataSource,
        )
    }
}


