package com.sandymist.android.debuglib.repository

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.sandymist.android.debuglib.db.DebugLibDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface RoomDBRepository {
    suspend fun getRoomDatabaseSize(context: Context): Long
    suspend fun getRoomDatabasesAndTables(context: Context): Map<String, List<String>>
    fun clear()
}

@Singleton
class RoomDBRepositoryImpl @Inject constructor(
): RoomDBRepository {
    override suspend fun getRoomDatabaseSize(context: Context) = withContext(Dispatchers.IO) {
        DebugLibDatabase.getDatabaseSize(context)
    }

    override suspend fun getRoomDatabasesAndTables(context: Context): Map<String, List<String>> = withContext(Dispatchers.IO) {
        val databasesPath = context.getDatabasePath("dummy").parentFile

        if (databasesPath?.exists() == true) {
            val dbFiles = databasesPath.listFiles()?.filter { it.exists() && it.isFile }
            val appDbFiles = dbFiles
                ?.filter { file ->
                    file.name.endsWith("-wal") && dbFiles.any { file.name.removeSuffix("-wal") == it.name }
                }?.map {
                    it.name.removeSuffix("-wal")
                }

            appDbFiles?.associate { dbName ->
                Timber.d("Checking database $dbName")
                val dbFile = File(databasesPath, dbName)

                // Filter for Room database files (usually ends with .db)
                if (dbFile.exists() && dbFile.isFile /* && dbName.endsWith(".db") */) {
                    val database = SQLiteDatabase.openDatabase(dbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
                    val tables = try {
                        val tables = getTablesFromDatabase(database)
                        database.close()
                        tables
                    } catch (exception: Exception) {
                        Timber.d("Unable open file in database directory: ${exception.message}")
                        emptyList()
                    }
                    dbName to tables
                } else {
                    dbName to listOf()
                }
            } ?: emptyMap()
        } else {
            emptyMap()
        }
    }

    private fun getTablesFromDatabase(database: SQLiteDatabase): List<String> {
        val tableNames = mutableListOf<String>()
        val cursor: Cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        cursor.use {
            while (it.moveToNext()) {
                tableNames.add(it.getString(0))
            }
        }
        return tableNames
    }

    override fun clear() {
    }
}
