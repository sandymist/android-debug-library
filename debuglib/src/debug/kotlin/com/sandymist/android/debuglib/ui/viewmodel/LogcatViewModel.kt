package com.sandymist.android.debuglib.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandymist.android.debuglib.model.Logcat
import com.sandymist.android.debuglib.repository.LogcatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogcatViewModel @Inject constructor(
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
