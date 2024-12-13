package dora.cache.repository

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dora.cache.RoomCacheHolderFactory

abstract class RoomDatabaseCacheRepository<T : Any, D : RoomDatabase>(context: Context)
    : BaseSuspendDatabaseCacheRepository<T, RoomCacheHolderFactory<T, D>>(context) {

// Rewrite it to specify the type of the model.
// 简体中文：重写它以指定model的类型。
// override fun getModelType(): Class<T> {
//     return super.getModelType()
// }

    /**
     * Rewrite it to specify the type of the Room database.
     * 简体中文：重写它以指定room数据库的类型。
     */
    abstract fun getDatabaseType() : Class<D>

    /**
     * Rewrite it to specify the name of the database.
     * 简体中文：重写它以指定数据库的名称。
     */
    abstract fun getDatabaseName() : String

    /**
     * Rewrite it to specify the method name of [RoomDatabase]'s getDao.
     * 简体中文：重写它以指定[RoomDatabase]的getDao的方法名。
     */
    abstract fun getDaoName() : String

    private fun getRoomDatabase() : D {
        return Room.databaseBuilder(context, getDatabaseType(), getDatabaseName()).build()
    }

    override fun createCacheHolderFactory(): RoomCacheHolderFactory<T, D> {
        return RoomCacheHolderFactory<T, D>(getRoomDatabase(), getDatabaseName())
    }
}