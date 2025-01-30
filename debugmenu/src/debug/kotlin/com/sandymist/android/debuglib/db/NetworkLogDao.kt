package com.sandymist.android.debuglib.db
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface NetworkLogDao {
//    @Insert
//    suspend fun insert(entity: NetworkLogEntity)
//
//    @Query("SELECT * FROM network_log ORDER BY timestamp DESC")
//    fun getAll(): Flow<List<NetworkLogEntity>>
//
//    @Query("SELECT * FROM network_log ORDER BY timestamp DESC")
//    suspend fun getAllEntities(): List<NetworkLogEntity>
//
//    @Query("SELECT * FROM network_log WHERE id = :id")
//    suspend fun getNetworkLog(id: String): NetworkLogEntity
//
//    @Query("DELETE FROM network_log")
//    suspend fun clearAll()
//}
