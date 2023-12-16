package dora.cache.holder

import android.annotation.SuppressLint
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import dora.cache.dao.IListRoomDao
import dora.db.OrmLog
import dora.db.builder.Condition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RoomListCacheHolder<M>(var db: RoomDatabase, var daoName: String,
                         var clazz: Class<M>) : ListCacheHolder<M>() {

    private lateinit var dao: IListRoomDao<M>

    override fun init() {
        dao = db.javaClass.getDeclaredMethod(daoName).invoke(db) as IListRoomDao<M>
    }

    override fun queryCache(condition: Condition): MutableList<M> {
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

    override fun addNewCache(models: MutableList<M>) {
        runBlocking {
            withContext(Dispatchers.IO) {
                val results = dao.insert(models)
                for (result in results) {
                    if (result > 0) {
                        continue
                    } else {
                        OrmLog.d("addNewCache:false")
                        return@withContext
                    }
                }
                OrmLog.d("addNewCache:true")
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