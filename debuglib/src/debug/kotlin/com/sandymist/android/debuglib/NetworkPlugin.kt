@file:Suppress("PackageDirectoryMismatch")
package com.sandymist.mobile.plugins.network

import com.sandymist.android.debuglib.DebugLib
import com.sandymist.android.debuglib.utils.OkHTTPToHAR
import okhttp3.Interceptor
import okhttp3.Response

@Suppress("unused")
class NetworkPlugin: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val startTime = System.nanoTime()
        val response: Response = chain.proceed(request)
        val endTime = System.nanoTime()

        val harEntry = OkHTTPToHAR.convertOkHTTPToHAR(request, response, endTime - startTime)

        DebugLib.insertNetworkLog(harEntry)

        return response
    }
}
