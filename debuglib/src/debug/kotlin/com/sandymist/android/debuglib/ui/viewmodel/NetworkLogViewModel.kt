package com.sandymist.android.debuglib.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sandymist.android.debuglib.model.HarEntry
import com.sandymist.android.debuglib.model.MockRequest
import com.sandymist.android.debuglib.repository.NetworkLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NetworkLogViewModel @Inject constructor(
    private val networkLogRepository: NetworkLogRepository,
): ViewModel() {
    val count = networkLogRepository.countFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0L)
    val searchString = networkLogRepository.searchString
    val networkLogList: StateFlow<List<HarEntry>> = networkLogRepository.networkLogList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    suspend fun getNetworkLog(id: Long) = networkLogRepository.getNetworkLog(id)

    suspend fun getNetworkLogEntries(searchString: String = "") = networkLogRepository.getNetworkLogEntries(searchString)

    fun setSearchString(searchString: String) {
        networkLogRepository.setSearchString(searchString)
    }

    fun clear() {
        networkLogRepository.clear()
    }

    suspend fun mockRequest(mockRequest: MockRequest) = networkLogRepository.mockRequest(mockRequest)

    suspend fun unMockRequest(path: String, method: String) = networkLogRepository.unMockRequest(path, method)

    suspend fun isMocked(path: String, method: String) = networkLogRepository.isMocked(path, method)

    fun getMocks() = networkLogRepository.getMocks()
}

class NetworkLogViewModelFactory(
    private val networkLogRepository: NetworkLogRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NetworkLogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NetworkLogViewModel(networkLogRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
