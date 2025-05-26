package app.getnuri.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Meal::class,
        UserFeedback::class,
        Ingredient::class,
        Symptom::class,
        HealthData::class,
        AnalysisResult::class // Added AnalysisResult
    ],
    version = 3, // Incremented version
    exportSchema = false
)
@TypeConverters(NuriTypeConverters::class) // Updated to use the consolidated converter
abstract class NuriDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun userFeedbackDao(): UserFeedbackDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun symptomDao(): SymptomDao
    abstract fun healthDataDao(): HealthDataDao
    abstract fun analysisResultDao(): AnalysisResultDao // Added AnalysisResultDao getter

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Recreate meals table
                db.execSQL("""
                    CREATE TABLE meals_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        inputType TEXT NOT NULL,
                        photoUri TEXT,
                        description TEXT,
                        notes TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO meals_new (id, timestamp, inputType, photoUri, description, notes)
                    SELECT id, timestamp, inputType, photoUri, description, notes FROM meals
                """.trimIndent())
                db.execSQL("DROP TABLE meals")
                db.execSQL("ALTER TABLE meals_new RENAME TO meals")

                // 2. Create ingredients table
                db.execSQL("""
                    CREATE TABLE ingredients (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        mealId INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        quantity REAL NOT NULL,
                        unit TEXT NOT NULL,
                        FOREIGN KEY(mealId) REFERENCES meals(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_ingredients_mealId ON ingredients(mealId)")

                // 3. Create symptoms table
                db.execSQL("""
                    CREATE TABLE symptoms (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        severity INTEGER NOT NULL,
                        notes TEXT
                    )
                """.trimIndent())

                // 4. Create health_data table
                db.execSQL("""
                    CREATE TABLE health_data (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        value TEXT NOT NULL,
                        unit TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        source TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `analysis_results` (" +
                           "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                           "`timestamp` INTEGER NOT NULL, " +
                           "`title` TEXT NOT NULL, " +
                           "`description` TEXT NOT NULL, " +
                           "`type` TEXT NOT NULL, " +
                           "`confidence` REAL NOT NULL, " +
                           "`relatedMealIds` TEXT, " +
                           "`relatedSymptomIds` TEXT, " +
                           "`relatedHealthDataIds` TEXT, " +
                           "`userFeedback` TEXT, " +
                           "`aiModelVersion` TEXT)")
            }
        }
    }
}
