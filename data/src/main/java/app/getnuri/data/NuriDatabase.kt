package app.getnuri.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Meal::class, UserFeedback::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(
    StringListConverter::class,
    IngredientListConverter::class,
    SymptomDetailListConverter::class
)
abstract class NuriDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun userFeedbackDao(): UserFeedbackDao

    companion object {
        /** Migration 1 → 2 : adds new columns while preserving data */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Meals
                database.execSQL(
                    "ALTER TABLE meals ADD COLUMN recognizedIngredients TEXT NOT NULL DEFAULT '[]'"
                )
                database.execSQL("ALTER TABLE meals ADD COLUMN lat REAL")
                database.execSQL("ALTER TABLE meals ADD COLUMN lon REAL")
                database.execSQL(
                    "ALTER TABLE meals ADD COLUMN processingState TEXT NOT NULL DEFAULT 'pending'"
                )
                database.execSQL("ALTER TABLE meals ADD COLUMN opticalModelVer TEXT")

                // UserFeedback
                database.execSQL("ALTER TABLE user_feedback ADD COLUMN energyScore INTEGER")
                database.execSQL("ALTER TABLE user_feedback ADD COLUMN via TEXT")
                database.execSQL("ALTER TABLE user_feedback ADD COLUMN moodScore INTEGER")
            }
        }
    }
}
