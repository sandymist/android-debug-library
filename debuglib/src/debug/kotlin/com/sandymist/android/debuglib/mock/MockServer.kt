package com.sandymist.android.debuglib.mock

import com.sandymist.android.debuglib.db.MockDao
import com.sandymist.android.debuglib.db.MockEntity
import com.sandymist.android.debuglib.model.MockRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockServer @Inject constructor(
    mockDao: MockDao,
) {
    private var server: MockWebServer? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val allMocksFlow = mockDao.getAll()
    private val _addedFlow = MutableSharedFlow<MockEntity>()
    private val _modifiedFlow = MutableSharedFlow<MockEntity>()
    private val _deletedFlow = MutableSharedFlow<MockEntity>()
    private var previousMocks: Map<Int, MockEntity> = emptyMap()

    init {
        scope.launch {
            server = MockWebServer().apply {
                dispatcher = object : Dispatcher() {
                    override fun dispatch(request: RecordedRequest): MockResponse {
                        val path = request.path
                        getMock(path ?: "", request.method ?: "")?.let { return it }
                        return MockResponse()
                            .setResponseCode(404)
                            .setBody("{\"error\": \"Not found\"}")
                    }
                }
                start(0)
                MockServerInfo.setPort(port)
            }
        }

        scope.launch {
            allMocksFlow.collect { currentList ->
                val currentMap = currentList.associateBy { it.id }

                // Detect added
                val added = currentMap.keys - previousMocks.keys
                added.forEach { id ->
                    val mockItem = currentMap.getValue(id)
                    addMockRequest(mockItem.path, mockItem.body, mockItem.code)
                    _addedFlow.emit(mockItem)
                }

                // Detect deleted
                val deleted = previousMocks.keys - currentMap.keys
                deleted.forEach { id ->
                    val mockItem = previousMocks.getValue(id)
                    deleteMockRequest(mockItem.path, mockItem.method)
                    _deletedFlow.emit(previousMocks.getValue(id))
                }

                // Detect modified -- TODO !!!
                val common = previousMocks.keys.intersect(currentMap.keys)
                common.forEach { id ->
                    val oldItem = previousMocks.getValue(id)
                    val newItem = currentMap.getValue(id)
                    if (oldItem != newItem) {
                        _modifiedFlow.emit(newItem)
                    }
                }

                previousMocks = currentMap
            }
        }
    }

    fun shutdown() {
        server?.shutdown()
        server = null
    }

    private fun addMockRequest(path: String, jsonBody: String, responseCode: Int = 200) {
        val response = MockResponse()
            .setResponseCode(responseCode)
            .setHeader("Content-Type", "application/json")
            .setBody(jsonBody)

        addMock(MockRequest(path, "GET", responseCode, jsonBody), response)
    }

    private fun deleteMockRequest(path: String, method: String) {
        val key = "$path/$method"
        mockList.remove(key)
    }

    companion object {
        private val mockList = mutableMapOf<String, MockResponse>()
        val MOCK_SERVER_PORT: Int
            get() = MockServerInfo.port

        private fun addMock(mockRequest: MockRequest, mockResponse: MockResponse) {
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
            return mockRequest
        }
    }
}

private object MockServerInfo {
    @Volatile
    var port: Int = -1
        private set

    fun setPort(p: Int) {
        port = p
    }
}
