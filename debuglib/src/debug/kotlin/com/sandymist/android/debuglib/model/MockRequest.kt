package com.sandymist.android.debuglib.model

data class MockRequest(
    val path: String,
    val method: String,
    val code: Int,
    val body: String,
) {
    override fun toString(): String {
        return "$code $method $path ${body.take(30)}"
    }
}
