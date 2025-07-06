package dora.cache

import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import dora.db.builder.Condition

object QueryUtils {

    fun createSQLiteQuery(tableName: String, condition: Condition): SupportSQLiteQuery {
        val query = SupportSQLiteQueryBuilder.builder(tableName)
            .selection(condition.selection, condition.selectionArgs)
            .groupBy(condition.groupBy)
            .orderBy(condition.orderBy)
            .limit(condition.limit)
            .having(condition.having).create()
        return query
    }
}