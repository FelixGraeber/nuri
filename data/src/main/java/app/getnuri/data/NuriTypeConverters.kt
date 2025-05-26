package app.getnuri.data

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ProvidedTypeConverter // Make this the main converter class
class NuriTypeConverters @Inject constructor() { // Assuming Hilt injection might be needed

    // For List<String>
    @TypeConverter
    fun fromStringListString(value: String?): List<String>? {
        return value?.let { Json.decodeFromString<List<String>>(it) }
    }

    @TypeConverter
    fun toStringListString(list: List<String>?): String? {
        return list?.let { Json.encodeToString(it) }
    }

    // For List<Long>
    @TypeConverter
    fun fromLongListString(list: List<Long>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun toLongListFromString(data: String?): List<Long>? {
        return data?.split(',')?.mapNotNull { it.toLongOrNull() }
    }
}
