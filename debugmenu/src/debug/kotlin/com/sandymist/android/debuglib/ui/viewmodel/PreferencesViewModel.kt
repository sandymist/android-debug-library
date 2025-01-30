package com.sandymist.android.debuglib.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sandymist.android.debuglib.model.PrefItem
import com.sandymist.android.debuglib.repository.PreferencesRepository

class PreferencesViewModel (
    private val preferencesRepository: PreferencesRepository,
): ViewModel() {
    val preferencesList = preferencesRepository.prefList

    suspend fun getAllPreferenceEntries() = preferencesRepository.getAllPreferencesEntries().map {
        if (it.type == "header") {
            PrefItem.Header(title = it.name)
        } else {
            PrefItem.Data(key = it.name, value = it.value)
        }
    }

    fun clear() {
        preferencesRepository.clear()
    }
}

class PreferencesViewModelFactory(
    private val preferencesRepository: PreferencesRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PreferencesViewModel(preferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
