package dora.cache.repository

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dora.cache.RoomCacheHolderFactory

abstract class RoomDatabaseCacheRepository<T : Any, D : RoomDatabase>(context: Context)
    : BaseSuspendDatabaseCacheRepository<T, RoomCacheHolderFactory<T, D>>(context) {

    @Volatile
    private var db: D? = null

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

    /**
     * Get RoomDatabase singleton with default configuration.
     * 简体中文：获取使用默认配置的Room数据库单例。
     */
    protected fun getRoomDatabase(): D {
        return db ?: synchronized(this) {
            db ?: buildDatabase { it /* fallbackToDestructiveMigration */ }.also { db = it }
        }
    }

    /**
     * Get RoomDatabase singleton with custom configuration.
     * 简体中文：获取使用自定义配置的Room数据库单例。
     *
     * @param config Lambda to configure the RoomDatabase.Builder.
     */
    protected fun getRoomDatabase(
        config: (RoomDatabase.Builder<D>) -> RoomDatabase.Builder<D>
    ): D {
        return db ?: synchronized(this) {
            db ?: buildDatabase(config).also { db = it }
        }
    }

    /**
     * Build RoomDatabase with custom configuration.
     * 简体中文：使用可配置Lambda创建Room数据库。
     */
    private fun buildDatabase(
        config: (RoomDatabase.Builder<D>) -> RoomDatabase.Builder<D>
    ): D {
        val builder = Room.databaseBuilder(
            context,
            getDatabaseType(),
            getDatabaseName()
        )
        return config(builder).build()
    }

    override fun createCacheHolderFactory(): RoomCacheHolderFactory<T, D> {
        return RoomCacheHolderFactory<T, D>(getRoomDatabase(), getDatabaseName())
    }
}