package app.getnuri.data

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NuriDatabaseTest {

    private lateinit var db: NuriDatabase
    private lateinit var mealDao: MealDao
    private lateinit var ingredientDao: IngredientDao
    // Add other DAOs if you test them here

    private val TEST_DB = "nuri-test.db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        NuriDatabase::class.java.canonicalName ?: NuriDatabase::class.java.simpleName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Before
    fun createDb() {
        // Using an in-memory database for regular DAO tests
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, NuriDatabase::class.java)
            .allowMainThreadQueries() // Only for testing
            .addTypeConverters(NuriTypeConverters()) // Add type converters
            .build()
        mealDao = db.mealDao()
        ingredientDao = db.ingredientDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        helper.closeWhenFinished(db) // Close the in-memory db via helper if it was used by it
                                     // or db.close() if only in-memory was used for this test.
                                     // For DAO tests, db.close() is fine.
                                     // For migration tests, helper manages db instances.
        if (this::db.isInitialized && db.isOpen) {
            db.close()
        }
    }

    // --- IngredientDao Tests ---
    @Test
    @Throws(Exception::class)
    fun insertAndGetIngredientsForMeal() = runBlocking {
        val meal = Meal(id = 1L, timestamp = System.currentTimeMillis(), inputType = "TEXT")
        mealDao.insertMeal(meal) // Corrected to insertMeal

        val ingredient1 = Ingredient(mealId = 1L, name = "Apple", quantity = 1.0, unit = "piece")
        val ingredient2 = Ingredient(mealId = 1L, name = "Banana", quantity = 2.0, unit = "pieces")
        ingredientDao.insertAll(listOf(ingredient1, ingredient2))

        val ingredients = ingredientDao.getIngredientsForMealFlow(1L).first()
        assertEquals(2, ingredients.size)
        assertTrue(ingredients.any { it.name == "Apple" })
        assertTrue(ingredients.any { it.name == "Banana" })
    }

    @Test
    @Throws(Exception::class)
    fun deleteIngredientsForMeal() = runBlocking {
        val meal = Meal(id = 1L, timestamp = System.currentTimeMillis(), inputType = "TEXT")
        mealDao.insertMeal(meal) // Corrected to insertMeal
        val ingredient1 = Ingredient(mealId = 1L, name = "Apple", quantity = 1.0, unit = "piece")
        ingredientDao.insertAll(listOf(ingredient1))

        var ingredients = ingredientDao.getIngredientsForMealFlow(1L).first()
        assertEquals(1, ingredients.size)

        ingredientDao.deleteIngredientsForMeal(1L)
        ingredients = ingredientDao.getIngredientsForMealFlow(1L).first()
        assertEquals(0, ingredients.size)
    }

    // --- Migration Tests ---
    @Test
    @Throws(IOException::class)
    fun testMigration1To2() {
        // Create a database with version 1 using the old schema for Meal
        helper.createDatabase(TEST_DB, 1).apply {
            // Schema for Meal v1 (approximated - actual column types matter)
            // rawExtractedIngredients, rawExtractedTriggers, userConfirmedIngredients, userConfirmedTriggers were List<String>
            // which would have been stored as TEXT via TypeConverter.
            // Need to ensure the type converter for List<String> is available if inserting data.
            // For MIGRATION_1_2, it's a destructive recreation, so exact data content might not be testable this way.
            execSQL("CREATE TABLE IF NOT EXISTS `meals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `inputType` TEXT NOT NULL, `photoUri` TEXT, `description` TEXT, `rawExtractedIngredients` TEXT NOT NULL, `rawExtractedTriggers` TEXT NOT NULL, `userConfirmedIngredients` TEXT NOT NULL, `userConfirmedTriggers` TEXT NOT NULL, `notes` TEXT)")
            // Insert some data if needed to check data preservation, though this migration recreates the table.
            // Example of inserting data that would use the StringListConverter (Json format for list of strings)
            // execSQL("INSERT INTO meals (timestamp, inputType, rawExtractedIngredients, rawExtractedTriggers, userConfirmedIngredients, userConfirmedTriggers) VALUES (0, 'TEXT', '[\"item1\"]', '[\"trigger1\"]', '[\"item1\"]', '[\"trigger1\"]')")
            close()
        }

        // Run migration to version 2
        val db_v2 = helper.runMigrationsAndValidate(TEST_DB, 2, true, NuriDatabase.MIGRATION_1_2)

        // Schema validation is done by runMigrationsAndValidate.
        // To perform data validation, you would need to query db_v2.
        // For MIGRATION_1_2, data from 'meals' is copied for common columns.
        // New tables (ingredients, symptoms, health_data) are created empty.
        db_v2.close()
    }
    
    @Test
    @Throws(IOException::class)
    fun testMigration2To3() {
        // Create a database with version 2
        helper.createDatabase(TEST_DB, 2).apply {
             // You might want to insert sample data into tables existing in v2 if your migration
             // depends on or modifies existing data. MIGRATION_2_3 only adds a new table.
            close()
        }


        // Run migration to version 3 and validate
        val db_v3 = helper.runMigrationsAndValidate(TEST_DB, 3, true, NuriDatabase.MIGRATION_2_3)
        
        // Check if analysis_results table was created by trying a simple query
        val cursor = db_v3.query("SELECT * FROM analysis_results")
        assertTrue(cursor.columnCount > 0) // Basic check that columns exist
        assertEquals(0, cursor.count) // Should be empty as it's a new table
        cursor.close()
        db_v3.close()
    }
}
