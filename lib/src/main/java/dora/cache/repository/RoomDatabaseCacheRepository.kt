package dora.cache.repository

import android.content.Context
import androidx.room.RoomDatabase
import dora.cache.RoomCacheHolderFactory

abstract class RoomDatabaseCacheRepository<T : Any, D : RoomDatabase>(context: Context)
    : BaseSuspendDatabaseCacheRepository<T, RoomCacheHolderFactory<T, D>>(context) {

    abstract fun getRoomDatabase() : D

    /**
     * 返回RoomDatabase的getDao的方法名。
     */
    abstract fun getDaoName() : String
}