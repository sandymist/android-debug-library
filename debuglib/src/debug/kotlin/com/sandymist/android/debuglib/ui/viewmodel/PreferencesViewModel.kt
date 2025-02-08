package com.sandymist.android.debuglib.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.sandymist.android.debuglib.model.DataListItem
import com.sandymist.android.debuglib.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
): ViewModel() {
    val preferencesList = preferencesRepository.prefList

    suspend fun getAllPreferenceEntries() = preferencesRepository.getAllPreferencesEntries().map {
        if (it.type == "header") {
            DataListItem.Header(title = it.name)
        } else {
            DataListItem.Data(key = it.name, value = it.value)
        }
    }

    fun clear() {
        preferencesRepository.clear()
    }
}
