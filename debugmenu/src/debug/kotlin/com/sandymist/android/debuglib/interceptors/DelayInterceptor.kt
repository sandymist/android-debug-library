package com.sandymist.android.debuglib.interceptors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class DelayInterceptor(private val delayMillis: Long) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        runBlocking(Dispatchers.IO) {
            delay(delayMillis)
        }

        return chain.proceed(chain.request())
    }
}
