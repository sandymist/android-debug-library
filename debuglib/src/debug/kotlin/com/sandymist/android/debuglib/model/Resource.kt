package com.sandymist.android.debuglib.model

sealed class Resource {
    data object Loading: Resource()
    data class Success(val data: Any): Resource()
    data class Error(val exception: Exception): Resource()
}
