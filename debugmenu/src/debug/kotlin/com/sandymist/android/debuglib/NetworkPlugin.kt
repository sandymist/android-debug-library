@file:Suppress("PackageDirectoryMismatch")
package com.sandymist.mobile.plugins.network

import com.sandymist.android.debuglib.DebugLib
import com.sandymist.android.debuglib.model.NetworkLog
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

@Suppress("unused")
class NetworkPlugin: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val url = request.url()
        val startTime = System.nanoTime()
        val response: Response = chain.proceed(request)
        val endTime = System.nanoTime()

        val contentType = response.header("Content-Type")
        val responseBody = if (contentType.isPrintable()) {
            val peekedBody = response.peekBody(1024 * 1024) // Peek up to 1 MB
            if (contentType.isContentTypeJson()) peekedBody.string().prettyPrintJson() else peekedBody.string()
        } else ""

        val requestHeaders = request.headers()
        val requestHeaderList = requestHeaders.names().map { name ->
            val value = requestHeaders[name]
            "$name: $value"
        }

        val responseHeaders = response.headers()
        val responseHeaderList = responseHeaders.names().map { name ->
            val value = responseHeaders[name]
            "$name: $value"
        }

        val networkLog = NetworkLog(
            id = UUID.randomUUID().toString(),
            responseCode = response.code(),
            url = url.toString(),
            method = request.method(),
            body = responseBody,
            requestHeaders = requestHeaderList,
            responseHeaders = responseHeaderList,
            timestamp = System.currentTimeMillis(),
            turnaroundTime = endTime - startTime,
        )
        DebugLib.insertNetworkLog(networkLog)

        return response
    }
}

fun String?.isContentTypeJson(): Boolean {
    return this?.startsWith("application/json") ?: false
}

fun String?.isPrintable(): Boolean {
    val printableTypes = listOf(
        "text/",         // All text types (e.g., text/plain, text/html)
        "application/json",
        "application/xml",
        "application/javascript",
        "application/x-www-form-urlencoded"
    )

    return printableTypes.any { this?.startsWith(it) == true }
}

fun String.prettyPrintJson(): String {
    return try {
        val jsonParser = Json { prettyPrint = true }
        val jsonElement = Json.parseToJsonElement(this)
        jsonParser.encodeToString(JsonObject.serializer(), jsonElement.jsonObject)
    } catch (e: Exception) {
        "Invalid JSON: $this - ${e.message}"
    }
}
