package com.sandymist.android.debuglib.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sandymist.android.debuglib.model.Logcat
import com.sandymist.android.debuglib.repository.LogcatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LogcatViewModel (
    private val logcatRepository: LogcatRepository,
): ViewModel() {
    private val _logcatList = MutableStateFlow<List<Logcat>>(emptyList())
    val logcatList = _logcatList.asStateFlow()

    init {
        viewModelScope.launch {
            logcatRepository.logcatList.collectLatest {
                _logcatList.emit(it)
            }
        }
    }

    suspend fun getAllLogcatEntries() = logcatRepository.getAllLogcatEntries()

    fun clear() {
        logcatRepository.clear()
    }
}

class LogcatViewModelFactory(
    private val logcatRepository: LogcatRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogcatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LogcatViewModel(logcatRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
