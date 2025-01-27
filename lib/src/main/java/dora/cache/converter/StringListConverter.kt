package dora.cache.converter

import androidx.room.TypeConverter

object StringListConverter {

    @TypeConverter
    fun fromList(list: List<String?>?): String? {
        return if (list == null) null else java.lang.String.join(",", list)
    }

    @TypeConverter
    fun toList(data: String?): List<String>? {
        return if (data == null) null else listOf(
            *data.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        )
    }
}
