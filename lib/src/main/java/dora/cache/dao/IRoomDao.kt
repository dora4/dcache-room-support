package dora.cache.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

interface IRoomDao<T> {

    @RawQuery
    fun select(query: SupportSQLiteQuery) : T?

    @Insert
    fun insert(model: T) : Long

    @Delete
    fun delete(model: T) : Int
}