package dora.cache.converter

import androidx.room.TypeConverter

object StringListConverter {

    @TypeConverter
    fun fromList(list: List<String?>?): String? =
        list?.filterNotNull()?.joinToString(",")

    @TypeConverter
    fun toList(data: String?): List<String>? =
        data?.split(",")?.filter { it.isNotEmpty() }
}
