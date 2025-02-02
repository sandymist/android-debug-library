package com.sandymist.android.debuglib.db
//
//import com.sandymist.android.debuglib.model.NetworkLog
//import io.objectbox.annotation.Entity
//import io.objectbox.annotation.Id
//
//@Entity
//data class NetworkLogEntity(
//    @Id
//    var id: Long = 0,
//    val responseCode: Int,
//    val url: String,
//    val method: String,
//    val requestHeaders: List<String>,
//    val responseHeaders: List<String>,
//    val body: String,
//    val timestamp: Long,
//    val turnaroundTime: Long,
//) {
//    fun toNetworkLog(): NetworkLog {
//        return NetworkLog(
//            id = id,
//            responseCode = responseCode,
//            url = url,
//            method = method,
//            requestHeaders = requestHeaders,
//            responseHeaders = responseHeaders,
//            body = body,
//            timestamp = timestamp,
//            turnaroundTime = turnaroundTime,
//        )
//    }
//
//    companion object {
//        fun fromNetworkLog(networkLog: NetworkLog): NetworkLogEntity {
//            return NetworkLogEntity(
//                id = networkLog.id,
//                responseCode = networkLog.responseCode,
//                url = networkLog.url,
//                method = networkLog.method,
//                requestHeaders = networkLog.requestHeaders,
//                responseHeaders = networkLog.responseHeaders,
//                body = networkLog.body,
//                timestamp = networkLog.timestamp,
//                turnaroundTime = networkLog.turnaroundTime,
//            )
//        }
//    }
//}
