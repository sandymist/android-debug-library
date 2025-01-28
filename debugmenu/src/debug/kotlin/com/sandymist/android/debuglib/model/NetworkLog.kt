package com.sandymist.android.debuglib.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkLog(
    val id: String,
    val responseCode: Int,
    val url: String,
    val method: String,
    val requestHeaders: List<String> = emptyList(),
    val responseHeaders: List<String> = emptyList(),
    val body: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val turnaroundTime: Long = 0L,
)
