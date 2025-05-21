package com.sandymist.android.debuglib

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.sandymist.android.debuglib.di.BoxStoreEntryPoint
import com.sandymist.android.debuglib.di.MockServerEntryPoint
import com.sandymist.android.debuglib.mock.MockServer
import com.sandymist.android.debuglib.model.HarEntry
import com.sandymist.android.debuglib.repository.NetworkLogRepository
import com.sandymist.android.debuglib.repository.NetworkLogRepositoryImpl
import com.sandymist.android.debuglib.ui.viewmodel.NetworkLogViewModel
import com.sandymist.android.debuglib.ui.viewmodel.NetworkLogViewModelFactory
import dagger.hilt.android.EntryPointAccessors
import io.objectbox.BoxStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/*
 * Helper object to collect and store network log
 * Called from Gradle plugin, can't use Hilt
 * TODO: Explore if Hilt can be used to inject
 */
object DebugLib {
    private val networkLogFlow = MutableStateFlow<HarEntry?>(null)
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var networkLogRepository: NetworkLogRepository
    private lateinit var networkLogViewModel: NetworkLogViewModel
    private val viewModelStore = ViewModelStore()
    private lateinit var mockServer: MockServer

    @Suppress("unused")
    fun init(context: Context) {
         val boxStore: BoxStore by lazy {
            EntryPointAccessors.fromApplication(
                context,
                BoxStoreEntryPoint::class.java
            ).getBoxStore()
        }
        val mockServer: MockServer by lazy {
            EntryPointAccessors.fromApplication(
                context,
                MockServerEntryPoint::class.java
            ).getMockServer()
        }
        this.mockServer = mockServer
        networkLogRepository = NetworkLogRepositoryImpl(boxStore)
        val networkLogViewModelFactory = NetworkLogViewModelFactory(networkLogRepository)
        networkLogViewModel = ViewModelProvider(viewModelStore, networkLogViewModelFactory)[NetworkLogViewModel::class.java]

        scope.launch {
            networkLogFlow.collect { networkLog ->
                networkLog?.let {
                    networkLogRepository.insert(networkLog)
                }
            }
        }
    }

    fun insertNetworkLog(harEntry: HarEntry) {
        scope.launch {
            networkLogFlow.emit(harEntry)
        }
    }

    @Suppress("unused")
    fun shutdown() {
        mockServer.shutdown()
        viewModelStore.clear()
        scope.cancel()
    }
}
