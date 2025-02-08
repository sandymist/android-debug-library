package com.sandymist.android.debuglib.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.sandymist.android.debuglib.repository.RoomDBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RoomDBViewModel @Inject constructor(
    private val roomDBRepository: RoomDBRepository,
) : ViewModel() {

    suspend fun getRoomDatabaseSize(context: Context): Long =
        roomDBRepository.getRoomDatabaseSize(context)

    suspend fun getRoomDatabasesAndTables(context: Context): Map<String, List<String>> =
        roomDBRepository.getRoomDatabasesAndTables(context)

    fun clear() {
        roomDBRepository.clear()
    }
}
