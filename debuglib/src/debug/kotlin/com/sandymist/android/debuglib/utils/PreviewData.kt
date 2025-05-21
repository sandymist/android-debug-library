package com.sandymist.android.debuglib.utils

import com.sandymist.android.debuglib.model.HarContent
import com.sandymist.android.debuglib.model.HarEntry
import com.sandymist.android.debuglib.model.HarHeader
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
        headers = listOf(
            HarHeader(
                name = "Content-Type",
                value = "text/html",
            ),
            HarHeader(
                name = "Accept",
                value = "application/json",
            )
        ),
        queryString = emptyList(),
    ),
    response = HarResponse(
        status = 200,
        statusText = "OK",
        httpVersion = "HTTP/1.1",
        headers = listOf(
            HarHeader(
                name = "Content-Type",
                value = "text/html",
            ),
            HarHeader(
                name = "Content-Length",
                value = "100",
            )
        ),
        content = HarContent(
            size = 100L,
            mimeType = "application/json",
            text = """
                {
                  "user": {
                    "id": 12345,
                    "name": "John Doe",
                    "email": "johndoe@example.com",
                    "address": {
                      "street": "123 Main St",
                      "city": "Springfield",
                      "state": "IL",
                      "zip": "62704"
                    },
                    "phoneNumbers": [
                      {
                        "type": "home",
                        "number": "555-1234"
                      },
                      {
                        "type": "work",
                        "number": "555-5678"
                      }
                    ],
                    "preferences": {
                      "newsletter": true,
                      "notifications": {
                        "email": true,
                        "sms": false
                      }
                    }
                  },
                  "orders": [
                    {
                      "orderId": 98765,
                      "date": "2023-08-22",
                      "status": "shipped",
                      "items": [
                        {
                          "productId": 111,
                          "name": "Widget A",
                          "quantity": 2,
                          "price": 19.99
                        },
                        {
                          "productId": 222,
                          "name": "Widget B",
                          "quantity": 1,
                          "price": 29.99
                        }
                      ],
                      "total": 69.97
                    },
                    {
                      "orderId": 87654,
                      "date": "2023-08-15",
                      "status": "delivered",
                      "items": [
                        {
                          "productId": 333,
                          "name": "Widget C",
                          "quantity": 3,
                          "price": 9.99
                        }
                      ],
                      "total": 29.97
                    }
                  ],
                  "metadata": {
                    "generatedAt": "2023-08-22T12:34:56Z",
                    "requestId": "abc123xyz",
                    "server": "api.example.com"
                  }
                }
            """.trimIndent(),
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
