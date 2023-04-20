package dora.cache.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

interface IListRoomDao<T> {

    @RawQuery
    suspend fun select(query: SupportSQLiteQuery) : MutableList<T>

    @Insert
    suspend fun insert(models: MutableList<T>) : MutableList<Long>

    @Delete
    suspend fun delete(models: MutableList<T>) : Int
}