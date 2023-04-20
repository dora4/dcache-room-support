package dora.cache.holder

import android.annotation.SuppressLint
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import dora.cache.dao.IRoomDao
import dora.db.OrmLog
import dora.db.builder.Condition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RoomCacheHolder<M>(var db: RoomDatabase, var daoName: String,
                         var clazz: Class<M>) : CacheHolder<M> {

    private lateinit var dao: IRoomDao<M>

    override fun init() {
        dao = db.javaClass.getDeclaredMethod(daoName).invoke(db) as IRoomDao<M>
    }

    override fun queryCache(condition: Condition): M? {
        return runBlocking {
            withContext(Dispatchers.IO) {
                val query = SupportSQLiteQueryBuilder.builder(clazz.simpleName)
                .selection(condition.selection, condition.selectionArgs)
                .groupBy(condition.groupBy)
                .orderBy(condition.orderBy)
                .limit(condition.limit)
                .having(condition.having).create()
                dao.select(query)
            }
        }
    }

    override fun removeOldCache(condition: Condition) {
        runBlocking {
            withContext(Dispatchers.IO) {
                val model = queryCache(condition)
                model?.let {
                    val ok = dao.delete(it) > 0
                    OrmLog.d("removeOldCache:$ok")
                }
            }
        }
    }

    override fun addNewCache(model: M) {
        runBlocking {
            withContext(Dispatchers.IO) {
                val ok = dao.insert(model) > 0
                OrmLog.d("addNewCache:$ok")
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun queryCacheSize(condition: Condition): Long {
        val query = RoomSQLiteQuery.acquire("SELECT * FROM " + clazz.simpleName + " WHERE "
                + condition.selection, condition.selectionArgs.size)
        val cursor = runBlocking {
            withContext(Dispatchers.IO) {
                db.query(query)
            }
        }
        return try {
            if (cursor.moveToFirst()) {
                cursor.getInt(0).toLong()
            } else 0
        } finally {
            cursor.close()
            query.release()
        }
    }
}