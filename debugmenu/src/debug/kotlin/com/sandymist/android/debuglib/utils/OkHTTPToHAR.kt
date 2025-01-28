package com.sandymist.android.debuglib.utils

import com.sandymist.android.debuglib.model.HarCache
import com.sandymist.android.debuglib.model.HarContent
import com.sandymist.android.debuglib.model.HarEntry
import com.sandymist.android.debuglib.model.HarHeader
import com.sandymist.android.debuglib.model.HarPostData
import com.sandymist.android.debuglib.model.HarQueryString
import com.sandymist.android.debuglib.model.HarRequest
import com.sandymist.android.debuglib.model.HarResponse
import com.sandymist.android.debuglib.model.HarTimings
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

object OkHTTPToHAR {
    fun convertOkHTTPToHAR(request: Request, response: Response, elapsedTime: Long): HarEntry {
        val peekedResponseBody = response.peekBody(1024 * 1024) // Peek up to 1 MB

        // Parse request details
        val harRequest = HarRequest(
            method = request.method(),
            url = request.url().toString(),
            httpVersion = "HTTP/1.1", // OkHttp uses HTTP/1.1 by default
            headers = request.headers().names().map { name ->
                HarHeader(name, request.header(name) ?: "")
            },
            queryString = request.url().queryParameterNames().map { name ->
                HarQueryString(name, request.url().queryParameter(name) ?: "")
            },
            postData = request.body()?.let { body ->
                val buffer = Buffer()
                body.writeTo(buffer)
                HarPostData(
                    mimeType = body.contentType()?.toString() ?: "",
                    text = buffer.readString(StandardCharsets.UTF_8)
                )
            },
            headersSize = -1, // OkHttp doesn't provide size details
            bodySize = request.body()?.contentLength()?.toInt() ?: 0
        )

        // Parse response details
        val harResponse = HarResponse(
            status = response.code(),
            statusText = response.message(),
            httpVersion = "HTTP/1.1", // OkHttp uses HTTP/1.1 by default
            headers = response.headers().names().map { name ->
                HarHeader(name, response.header(name) ?: "")
            },
            content = peekedResponseBody.let { body ->
                body.source().request(Long.MAX_VALUE) // Buffer entire body
                val content = body.source().buffer.clone()
                val size = content.completeSegmentByteCount()
                val contentString = content.readString(StandardCharsets.UTF_8)
                if (size > 1024) {
                    //val blob = contentString.compressString()
                    HarContent(
                        size = size,
                        mimeType = body.contentType()?.toString() ?: "",
                        text = "Content too long, more than 1KB"
                        //blob = blob,
                    )
                } else {
                    HarContent(
                        size = size,
                        mimeType = body.contentType()?.toString() ?: "",
                        text = contentString
                    )
                }
            },
            redirectURL = response.header("Location") ?: "",
            headersSize = -1, // OkHttp doesn't provide size details
            bodySize = peekedResponseBody.contentLength().toInt()
        )

        // Generate HAR entry
        val harEntry = HarEntry(
            id = UUID.randomUUID().toString(),
            startedDateTime = getIso8601Timestamp(),
            time = elapsedTime,
            request = harRequest,
            response = harResponse,
            cache = HarCache(),
            timings = HarTimings(
                blocked = -1,
                dns = -1,
                connect = -1,
                send = 0,
                wait = elapsedTime,
                receive = 0,
                ssl = -1
            ),
            serverIPAddress = response.handshake()?.peerPrincipal()?.name,
            connection = response.header("Connection")
        )

        return harEntry
    }

    private fun getIso8601Timestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(Date())
    }
}
