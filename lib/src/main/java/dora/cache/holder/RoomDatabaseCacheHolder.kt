package dora.cache.holder

import android.annotation.SuppressLint
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import dora.cache.QueryUtils
import dora.cache.dao.IRoomDao
import dora.db.OrmLog
import dora.db.builder.Condition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomDatabaseCacheHolder<M>(
    private var db: RoomDatabase,
    private var daoName: String,
    private var clazz: Class<M>) : SuspendDatabaseCacheHolder<M> {

    private lateinit var dao: IRoomDao<M>

    override fun init() {
        dao = db.javaClass.getDeclaredMethod(daoName).invoke(db) as IRoomDao<M>
    }

    override suspend fun queryCache(condition: Condition): M? {
        return withContext(Dispatchers.IO) {
            val query = QueryUtils.createSQLiteQuery(clazz.simpleName, condition)
            OrmLog.d("SQL: ${query.sql}")
            OrmLog.d("Args: ${condition.selectionArgs.joinToString(",")}")
            dao.select(query)
        }
    }

    override suspend fun removeOldCache(condition: Condition) {
        withContext(Dispatchers.IO) {
            val model = queryCache(condition)
            model?.let {
                val ok = dao.delete(it) > 0
                OrmLog.d("removeOldCache:$ok")
            }
        }
    }

    override suspend fun addNewCache(model: M) {
        withContext(Dispatchers.IO) {
            val ok = dao.insert(model) > 0
            OrmLog.d("addNewCache:$ok")
        }
    }

    @SuppressLint("RestrictedApi")
    override suspend fun queryCacheSize(condition: Condition): Long {
        val query = if (condition.selection == "") {
            RoomSQLiteQuery.acquire("SELECT COUNT(*) FROM " + clazz.simpleName, 0)

        } else {
            RoomSQLiteQuery.acquire("SELECT COUNT(*) FROM " + clazz.simpleName + " WHERE "
                    + condition.selection, condition.selectionArgs.size)

        }
        val cursor = withContext(Dispatchers.IO) {
            db.query(query)
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