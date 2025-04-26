package com.sandymist.android.debuglib.mock
//
//class MockServerManager {
//    init {
//        // Initialize and start the server
//        val mockServer = MockServer()
//        mockServer.start()
//
//// Configure responses for specific GET requests
//        mockServer.mockGetRequest("/api/users",
//            """{"users": [{"id": 1, "name": "John"}, {"id": 2, "name": "Jane"}]}"""
//        )
//
//        mockServer.mockGetRequest("/api/users/1",
//            """{"id": 1, "name": "John", "email": "john@example.com"}"""
//        )
//
//// Add a response with a delay to simulate slow network
//        mockServer.mockGetRequestWithDelay("/api/slow-endpoint",
//            """{"result": "This response was delayed"}""",
//            delayMs = 3000 // 3 seconds delay
//        )
//
//// Configure a response with specific query parameters
//        val params = mapOf(
//            "category" to "books",
//            "sort" to "price"
//        )
//        mockServer.mockGetRequestWithQueryParams("/api/products",
//            params,
//            """{"products": [{"id": 1, "name": "Book 1", "price": 9.99}]}"""
//        )
//
//// Get the base URL to use in your app
//        val baseUrl = mockServer.getUrl()
//
//// Later, shut down the server when you're done
//        mockServer.shutdown()
//    }
//}