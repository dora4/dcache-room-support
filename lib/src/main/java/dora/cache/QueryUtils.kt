package dora.cache

import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import dora.db.builder.Condition

object QueryUtils {

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