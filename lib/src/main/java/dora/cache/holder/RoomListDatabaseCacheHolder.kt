package dora.cache.holder

import android.annotation.SuppressLint
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import dora.cache.QueryUtils
import dora.cache.dao.IListRoomDao
import dora.db.OrmLog
import dora.db.builder.Condition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomListDatabaseCacheHolder<M>(
    private var db: RoomDatabase,
    private var daoName: String,
    private var clazz: Class<M>) : SuspendListDatabaseCacheHolder<M>() {

    private lateinit var dao: IListRoomDao<M>

    override fun init() {
        dao = db.javaClass.getDeclaredMethod(daoName).invoke(db) as IListRoomDao<M>
    }

    override suspend fun queryCache(condition: Condition): MutableList<M> {
        return withContext(Dispatchers.IO) {
            val query = QueryUtils.createSQLiteQuery(clazz.simpleName, condition)
            OrmLog.d("SQL: ${query.sql}")
            OrmLog.d("Args: ${condition.selectionArgs.joinToString(",")}")
            dao.select(query)
        }
    }

    override suspend fun removeOldCache(condition: Condition) {
        withContext(Dispatchers.IO) {
            val models = queryCache(condition)
            models.let {
                val ok = dao.delete(it) > 0
                OrmLog.d("removeOldCache:$ok")
            }
        }
    }

    override suspend fun addNewCache(models: MutableList<M>) {
        withContext(Dispatchers.IO) {
            val results = dao.insert(models)
            for (i in 0 until results.size) {
                val result = results[i]
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