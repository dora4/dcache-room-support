package dora.cache.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

interface IRoomDao<T> {

    @RawQuery
    suspend fun select(query: SupportSQLiteQuery) : T

    @Insert
    suspend fun insert(model: T) : Long

    @Delete
    suspend fun delete(model: T) : Int
}