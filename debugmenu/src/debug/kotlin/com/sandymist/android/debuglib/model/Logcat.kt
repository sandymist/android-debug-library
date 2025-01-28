package com.sandymist.android.debuglib.model

import kotlinx.serialization.Serializable

@Serializable
data class Logcat(
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
)
