@file:Suppress("PackageDirectoryMismatch")
package com.sandymist.mobile.plugins.network

import android.annotation.SuppressLint
import android.util.Log
import com.sandymist.android.debuglib.DebugLib
import com.sandymist.android.debuglib.mock.MockServer
import com.sandymist.android.debuglib.mock.MockServer.Companion.MOCK_SERVER_PORT
import com.sandymist.android.debuglib.utils.OkHTTPToHAR
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.mockwebserver.MockResponse

@Suppress("unused")
class NetworkPlugin: Interceptor {
    @SuppressLint("LogNotTimber")
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = shouldMockRequest(
            path = request.url.encodedPath,
            method = request.method,
        )?.let {
            val mockUrl = "http://localhost:${MOCK_SERVER_PORT}".toHttpUrl()
            request.newBuilder()
                .url(mockUrl.newBuilder()
                    .encodedPath(request.url.encodedPath)
                    .encodedQuery(request.url.encodedQuery)
                    .build())
                .build()
        } ?: run {
            request
        }
        val startTime = System.nanoTime()
        val response: Response = chain.proceed(request)
        val endTime = System.nanoTime()

        Log.e("NetworkPlugin", "++++ intercept: ${request.url}")
        val harEntry = OkHTTPToHAR.convertOkHTTPToHAR(request, response, endTime - startTime)

        DebugLib.insertNetworkLog(harEntry)

        return response
    }

    private fun shouldMockRequest(path: String, method: String): MockResponse? {
        return MockServer.isMocked(path, method)
    }
}
