package dora.cache

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import dora.db.builder.Condition

object RoomUtils {

    inline fun <reified DB : RoomDatabase> getDB(context: Context, databaseName: String) : DB {
        return Room.databaseBuilder(
            context,
            DB::class.java,
            databaseName
        ).build()
    }

    @JvmStatic
    fun <DB : RoomDatabase> getDB(context: Context, databaseType: Class<DB>, databaseName: String) : DB {
        return Room.databaseBuilder(
            context,
            databaseType,
            databaseName
        ).build()
    }

    private fun cleanClause(clause: String?): String? {
        return clause
            ?.replace(Regex("^(HAVING|GROUP BY|ORDER BY|LIMIT)\\s*"), "")
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }

    fun createSQLiteQuery(tableName: String, condition: Condition): SupportSQLiteQuery {
        val query = SupportSQLiteQueryBuilder.builder(tableName)
            .selection(condition.selection, condition.selectionArgs)
            .groupBy(cleanClause(condition.groupBy))
            .orderBy(cleanClause(condition.orderBy))
            .limit(cleanClause(condition.limit))
            .having(cleanClause(condition.having))
            .create()
        return query
    }
}