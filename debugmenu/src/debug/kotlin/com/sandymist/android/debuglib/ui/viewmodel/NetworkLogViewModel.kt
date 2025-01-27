package com.sandymist.android.debuglib.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sandymist.android.debuglib.model.NetworkLog
import com.sandymist.android.debuglib.repository.NetworkLogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NetworkLogViewModel (
    private val networkLogRepository: NetworkLogRepository,
): ViewModel() {
    private val _networkLogList = MutableStateFlow<List<NetworkLog>>(emptyList())
    val networkLogList = _networkLogList.asStateFlow()

    init {
        viewModelScope.launch {
            networkLogRepository.networkLogList.collectLatest {
                _networkLogList.emit(it)
            }
        }
    }

    suspend fun getNetworkLog(id: String) = networkLogRepository.getNetworkLog(id)

    fun clear() {
        networkLogRepository.clear()
    }
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
