package com.sandymist.android.debuglib.model

data class MockItem(
    val path: String,
    val method: String,
    val body: String,
    val code: Int,
    val createdAt: Long,
    val enabled: Boolean,
)
