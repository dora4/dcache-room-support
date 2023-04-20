package dora.cache.repository

import android.content.Context
import androidx.room.RoomDatabase
import dora.cache.holder.*

abstract class RoomDatabaseCacheRepository<T, D : RoomDatabase>(context: Context)
    : BaseDatabaseCacheRepository<T>(context) {

    override fun createCacheHolder(clazz: Class<T>): CacheHolder<T> {
        return RoomCacheHolder<T>(getRoomDatabase(), getDaoName(), clazz)
    }

    override fun createListCacheHolder(clazz: Class<T>): CacheHolder<MutableList<T>> {
        return RoomListCacheHolder<T>(getRoomDatabase(), getDaoName(), clazz)
    }

    abstract fun getRoomDatabase() : D

    /**
     * 返回RoomDatabase的getDao的方法名。
     */
    abstract fun getDaoName() : String
}