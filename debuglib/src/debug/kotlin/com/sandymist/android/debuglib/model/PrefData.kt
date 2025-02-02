package com.sandymist.android.debuglib.model

import kotlinx.serialization.Serializable

@Serializable
data class PrefData(
    val name: String,
    val value: String,
    val type: String,
    val createdAt: Long = System.currentTimeMillis(),
)
