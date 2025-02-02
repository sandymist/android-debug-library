package com.sandymist.android.debuglib.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogcatDao {
    @Insert
    suspend fun insert(entity: LogcatEntity)

    @Query("SELECT * FROM $LOGCAT_TABLE_NAME ORDER BY createdAt DESC")
    fun getAll(): Flow<List<LogcatEntity>>

    @Query("SELECT * FROM $LOGCAT_TABLE_NAME ORDER BY createdAt DESC")
    suspend fun getAllEntities(): List<LogcatEntity>

    @Query("DELETE FROM $LOGCAT_TABLE_NAME")
    suspend fun clearAll()
}
