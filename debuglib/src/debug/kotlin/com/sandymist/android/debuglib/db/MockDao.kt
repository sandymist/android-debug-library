package com.sandymist.android.debuglib.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MockDao {
    @Insert
    suspend fun insert(entity: MockEntity)

    @Query("SELECT * FROM $MOCK_TABLE_NAME ORDER BY createdAt DESC")
    fun getAll(): Flow<List<MockEntity>>

    @Query("SELECT * FROM $MOCK_TABLE_NAME ORDER BY createdAt DESC")
    suspend fun getAllEntities(): List<MockEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM $MOCK_TABLE_NAME WHERE path = :path AND method = :method)")
    suspend fun exists(path: String, method: String): Boolean

    @Query("SELECT * FROM $MOCK_TABLE_NAME WHERE path = :path AND method = :method")
    suspend fun get(path: String, method: String): MockEntity?

    @Query("DELETE FROM $MOCK_TABLE_NAME WHERE path = :path AND method = :method")
    suspend fun delete(path: String, method: String)

    @Query("DELETE FROM $MOCK_TABLE_NAME")
    suspend fun clearAll()
}
