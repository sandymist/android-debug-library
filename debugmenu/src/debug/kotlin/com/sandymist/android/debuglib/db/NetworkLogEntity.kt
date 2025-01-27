package com.sandymist.android.debuglib.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sandymist.android.debuglib.model.NetworkLog

@Entity(tableName = "network_log")
@TypeConverters(Converters::class)
data class NetworkLogEntity(
    @PrimaryKey
    val id: String,
    val responseCode: Int,
    val url: String,
    val method: String,
    val requestHeaders: List<String>,
    val responseHeaders: List<String>,
    val body: String,
    val timestamp: Long,
    val turnaroundTime: Long,
) {
    fun toNetworkLog(): NetworkLog {
        return NetworkLog(
            id = id,
            responseCode = responseCode,
            url = url,
            method = method,
            requestHeaders = requestHeaders,
            responseHeaders = responseHeaders,
            body = body,
            timestamp = timestamp,
            turnaroundTime = turnaroundTime,
        )
    }

    companion object {
        fun fromNetworkLog(networkLog: NetworkLog): NetworkLogEntity {
            return NetworkLogEntity(
                id = networkLog.id,
                responseCode = networkLog.responseCode,
                url = networkLog.url,
                method = networkLog.method,
                requestHeaders = networkLog.requestHeaders,
                responseHeaders = networkLog.responseHeaders,
                body = networkLog.body,
                timestamp = networkLog.timestamp,
                turnaroundTime = networkLog.turnaroundTime,
            )
        }
    }
}
