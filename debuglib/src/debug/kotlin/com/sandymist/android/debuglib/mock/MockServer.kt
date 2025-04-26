package com.sandymist.android.debuglib.mock

import com.sandymist.android.debuglib.model.MockRequest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import timber.log.Timber
import javax.inject.Inject

data class MockItem(
    val path: String,
    val method: String,
)

typealias MocksList = List<MockItem>

class MockServer @Inject constructor() {
    private var server: MockWebServer? = null

    fun start(port: Int = MOCK_SERVER_PORT) {
        server = MockWebServer().apply {
            // Set a custom dispatcher that handles specific paths
            dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    val path = request.path

                    // Check if we have a specific response for this path
                    getMock(path ?: "", request.method ?: "")?.let { return it }

//                    // Check if we have a response that matches the beginning of the path
//                    for ((registeredPath, response) in pathToResponseMap) {
//                        if (path?.startsWith(registeredPath) == true) {
//                            return response
//                        }
//                    }

                    // Default response if no specific handler is found
                    return MockResponse()
                        .setResponseCode(404)
                        .setBody("{\"error\": \"Not found\"}")
                }
            }

            // Start the server on the specified port
            start(port)
        }
    }

    fun shutdown() {
        server?.shutdown()
        server = null
    }

    // Add a response for a specific GET path
    fun mockGetRequest(path: String, jsonBody: String, responseCode: Int = 200) {
        val response = MockResponse()
            .setResponseCode(responseCode)
            .setHeader("Content-Type", "application/json")
            .setBody(jsonBody)

        addMockRequest(MockRequest(path, "GET", responseCode, jsonBody), response)
    }

    fun unMockRequest(path: String, method: String) {
        val key = "$path/$method"
        mockList.remove(key)
    }

    fun isMocked(path: String, method: String): Boolean {
        val key = "$path/$method"
        return mockList.containsKey(key)
    }

    fun getMocks() = mockList.map {
        MockItem(
            path = it.key.split("/")[1],
            method = it.key.split("/")[2],
        )
    }

//    // Add a response with delay to simulate network latency
//    fun mockGetRequestWithDelay(path: String, jsonBody: String, responseCode: Int = 200, delayMs: Long) {
//        val response = MockResponse()
//            .setResponseCode(responseCode)
//            .setHeader("Content-Type", "application/json")
//            .setBody(jsonBody)
//            .setBodyDelay(delayMs, TimeUnit.MILLISECONDS)
//
//        pathToResponseMap[path] = response
//    }
//
//    // For more complex matching based on query parameters
//    fun mockGetRequestWithQueryParams(
//        path: String,
//        queryParams: Map<String, String>,
//        jsonBody: String,
//        responseCode: Int = 200
//    ) {
//        val fullPath = "$path?${mapToQueryString(queryParams)}"
//        mockGetRequest(fullPath, jsonBody, responseCode)
//    }
//
//    private fun mapToQueryString(queryParams: Map<String, String>): String {
//        return queryParams.entries.joinToString("&") { (key, value) ->
//            "$key=$value"
//        }
//    }
//
//    // Record and inspect requests
//    fun getRequestCount(): Int {
//        return server?.requestCount ?: 0
//    }
//
//    fun takeRequest(): RecordedRequest? {
//        return server?.takeRequest()
//    }

    companion object {
        private val mockList = mutableMapOf<String, MockResponse>()
        const val MOCK_SERVER_PORT = 8080

        private fun addMockRequest(mockRequest: MockRequest, mockResponse: MockResponse) {
            val key = mockRequest.path + "/" + mockRequest.method
            mockList[key] = mockResponse
        }

        private fun getMock(path: String, method: String): MockResponse? {
            val key = "$path/$method"
            return mockList[key]
        }

        fun isMocked(path: String, method: String): MockResponse? {
            val key = "$path/$method"
            val mockRequest = mockList[key]
            Timber.e("++++ Mock: $mockRequest")
            return mockRequest
        }
    }
}
