package dora.cache

import androidx.room.RoomDatabase
import dora.cache.factory.DatabaseCacheHolderFactory
import dora.cache.holder.CacheHolder
import dora.cache.holder.RoomDatabaseCacheHolder
import dora.cache.holder.RoomListDatabaseCacheHolder

class RoomCacheHolderFactory<T, D : RoomDatabase>(private var db: D,
                                                           private var daoName: String) :
    DatabaseCacheHolderFactory<T>() {

    override fun createCacheHolder(clazz: Class<T>): CacheHolder<T> {
        return RoomDatabaseCacheHolder(db, daoName, clazz)
    }

    override fun createListCacheHolder(clazz: Class<T>): CacheHolder<MutableList<T>> {
        return RoomListDatabaseCacheHolder(db, daoName, clazz)
    }
}