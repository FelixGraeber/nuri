package app.getnuri.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object IngredientListConverter {
    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromList(value: List<Ingredient>): String = gson.toJson(value)

    @TypeConverter
    @JvmStatic
    fun toList(value: String): List<Ingredient> {
        val type = object : TypeToken<List<Ingredient>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}

object SymptomDetailListConverter {
    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromList(value: List<SymptomDetail>): String = gson.toJson(value)

    @TypeConverter
    @JvmStatic
    fun toList(value: String): List<SymptomDetail> {
        val type = object : TypeToken<List<SymptomDetail>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
} 