package com.sandymist.android.debuglib.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sandymist.android.debuglib.model.HarEntry
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
    val networkLogList: StateFlow<List<HarEntry>> = networkLogRepository.networkLogList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    suspend fun getNetworkLog(id: Long) = networkLogRepository.getNetworkLog(id)

    suspend fun getAllNetworkLogEntries() = networkLogRepository.getAllNetworkLogEntries()

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
