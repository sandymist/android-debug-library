package com.sandymist.android.debuglib.utils

import com.sandymist.android.debuglib.model.HarContent
import com.sandymist.android.debuglib.model.HarEntry
import com.sandymist.android.debuglib.model.HarRequest
import com.sandymist.android.debuglib.model.HarResponse
import com.sandymist.android.debuglib.model.HarTimings

val TEST_HAR_ENTRY = HarEntry(
    id = 100,
    startedDateTime = "2023-08-22T1",
    time = 10L,
    request = HarRequest(
        method = "GET",
        url = "https://www.google.com",
        httpVersion = "HTTP/1.1",
        headers = emptyList(),
        queryString = emptyList(),
    ),
    response = HarResponse(
        status = 200,
        statusText = "OK",
        httpVersion = "HTTP/1.1",
        headers = emptyList(),
        content = HarContent(
            size = 100L,
            mimeType = "text/html",
            text = "Hello World",
        ),
        redirectURL = "",
        headersSize = 100,
        bodySize = 200,
    ),
    timings = HarTimings(
        blocked = 10L,
        dns = 10L,
        connect = 10L,
        send = 10L,
        wait = 10L,
        receive = 10L,
        ssl = 10L,
    ),
    createdAt = 10L,
)
