package com.sandymist.android.debuglib.model

import com.sandymist.android.debuglib.utils.decompressString
import com.sandymist.android.debuglib.utils.isContentTypeJson
import com.sandymist.android.debuglib.utils.isPrintable
import com.sandymist.android.debuglib.utils.prettyPrintJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class HarEntry(
    val id: String,
    val startedDateTime: String, // ISO 8601 timestamp
    val time: Long, // Total time in milliseconds
    val request: HarRequest,
    val response: HarResponse,
    val cache: HarCache = HarCache(),
    val timings: HarTimings,
    val serverIPAddress: String? = null, // Optional
    val connection: String? = null, // Optional
    val createdAt: Long = System.currentTimeMillis(),
) {
    override fun toString(): String {
        val json = Json { prettyPrint = true }
        val harString = json.encodeToString(serializer(), this)
        return harString
    }
}

@Serializable
data class HarRequest(
    val method: String, // HTTP method (e.g., GET, POST)
    val url: String, // Request URL
    val httpVersion: String, // HTTP version (e.g., HTTP/1.1)
    val headers: List<HarHeader>, // List of request headers
    val queryString: List<HarQueryString> = emptyList(), // Query parameters
    val postData: HarPostData? = null, // Optional, for POST/PUT requests
    val headersSize: Int = -1, // Size of headers (in bytes), -1 if unknown
    val bodySize: Int = -1 // Size of body (in bytes), -1 if unknown
)

@Serializable
data class HarResponse(
    val status: Int, // HTTP status code
    val statusText: String, // HTTP status text (e.g., OK, Not Found)
    val httpVersion: String, // HTTP version (e.g., HTTP/1.1)
    val headers: List<HarHeader>, // List of response headers
    val content: HarContent, // Response content
    val redirectURL: String, // Redirect URL, if any
    val headersSize: Int = -1, // Size of headers (in bytes), -1 if unknown
    val bodySize: Int = -1 // Size of body (in bytes), -1 if unknown
)

@Serializable
data class HarHeader(
    val name: String, // Header name
    val value: String // Header value
) {
    override fun toString(): String {
        return "$name: $value"
    }
}

@Serializable
data class HarQueryString(
    val name: String, // Query parameter name
    val value: String // Query parameter value
)

@Serializable
data class HarPostData(
    val mimeType: String, // MIME type of the request body
    val text: String // Request body as text
)

@Serializable
data class HarContent(
    val size: Long, // Size of the response body (in bytes)
    val mimeType: String, // MIME type of the response body
    val text: String? = null, // Response body as text
    val encoding: String? = null, // Optional, e.g., "base64"
    val blob: ByteArray? = null,
) {
    override fun toString(): String {
        val content = blob?.let {
            blob.decompressString()
        } ?: text
        return if (mimeType.isPrintable() && content?.isNotEmpty() == true) {
            if (mimeType.isContentTypeJson()) content.prettyPrintJson() else content
        } else "<No content or not printable>"
    }
}

@Serializable
data class HarCache(
    val beforeRequest: HarCacheEntry? = null, // State before request
    val afterRequest: HarCacheEntry? = null, // State after request
    val expires: String? = null // Expiry time, if available
)

@Serializable
data class HarCacheEntry(
    val lastAccess: String? = null, // Last access time
    val eTag: String? = null, // ETag
    val hitCount: Int? = null // Cache hit count
)

@Serializable
data class HarTimings(
    val blocked: Long = -1, // Time blocked (in ms), -1 if unknown
    val dns: Long = -1, // DNS resolution time (in ms), -1 if unknown
    val connect: Long = -1, // Connection time (in ms), -1 if unknown
    val send: Long = 0, // Time taken to send the request
    val wait: Long = 0, // Time waiting for the response
    val receive: Long = 0, // Time to receive the response
    val ssl: Long = -1 // SSL handshake time, -1 if not applicable
)
