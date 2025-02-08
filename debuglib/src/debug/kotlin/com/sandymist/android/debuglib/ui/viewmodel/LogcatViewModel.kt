package com.sandymist.android.debuglib.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandymist.android.debuglib.model.Logcat
import com.sandymist.android.debuglib.repository.LogcatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LogcatViewModel @Inject constructor(
    private val logcatRepository: LogcatRepository,
): ViewModel() {
    val logcatList: StateFlow<List<Logcat>> = logcatRepository.logcatList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    suspend fun getAllLogcatEntries() = logcatRepository.getAllLogcatEntries()

    fun clear() {
        logcatRepository.clear()
    }
}
