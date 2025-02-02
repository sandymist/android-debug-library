package com.sandymist.android.debuglib.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preferencesEntity: PreferencesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(preferencesEntities: List<PreferencesEntity>)

    @Query("SELECT * FROM preferences ORDER BY createdAt DESC")
    fun getAll(): Flow<List<PreferencesEntity>>

    @Query("SELECT * FROM preferences ORDER BY createdAt DESC")
    suspend fun getAllEntities(): List<PreferencesEntity>

    @Query("DELETE FROM preferences")
    suspend fun clearAll()
}
