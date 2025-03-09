@file:Suppress("DEPRECATION")
package com.sandymist.android.debuglib.repository

import android.content.Context
import android.preference.PreferenceManager
import com.sandymist.android.debuglib.db.PreferencesDao
import com.sandymist.android.debuglib.db.PreferencesEntity
import com.sandymist.android.debuglib.di.AppContext
import com.sandymist.android.debuglib.model.PrefData
import com.sandymist.android.debuglib.model.DataListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface PreferencesRepository {
    val prefList: StateFlow<List<DataListItem>>

    suspend fun getAllPreferencesEntries(): List<PrefData>
    fun clear()
}

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    @AppContext private val context: Context,
    private val preferencesDao: PreferencesDao,
): PreferencesRepository {
    private val _prefList = MutableStateFlow<List<DataListItem>>(emptyList())
    override val prefList = _prefList.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val items = mutableListOf<DataListItem>()
            val defaultPrefs = sharedPreferences.all
            if (defaultPrefs.isNotEmpty()) {
                items.add(DataListItem.Header("Default"))
                defaultPrefs.forEach { (key, value) ->
                    items.add(DataListItem.Data(key, value.toString()))
                }
            }

            val sharedPrefsDir = File(context.filesDir.parent, "shared_prefs")
            if (sharedPrefsDir.exists() && sharedPrefsDir.isDirectory) {
                val sharedPrefsFiles = sharedPrefsDir.listFiles()
                sharedPrefsFiles?.forEach { file ->
                    val prefsName = file.nameWithoutExtension
                    items.add(DataListItem.Header(prefsName))
                    val sharedPrefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                    val allEntries = sharedPrefs.all
                    for ((key, value) in allEntries) {
                        items.add(DataListItem.Data(key, value?.toString() ?: "Not set"))
                    }
                }
            }

            preferencesDao.insertAll(items.map {
                when (it) {
                    is DataListItem.Header -> {
                        val data = PrefData(name = it.title, value = "", type = "header")
                        PreferencesEntity.fromPrefData(data)
                    }
                    is DataListItem.Data -> {
                        val data = PrefData(name = it.key, value = it.value, type = "")
                        PreferencesEntity.fromPrefData(data)
                    }
                }
            })
            _prefList.emit(items)

            // TODO: not fully supported
            val dataStoreDir = File(context.filesDir, "datastore")
            if (dataStoreDir.exists() && dataStoreDir.isDirectory) {
                val dataStoreFiles = dataStoreDir.listFiles()
                dataStoreFiles?.forEach { file ->
                    println("DataStore file: ${file.name}")
                }
            }
        }
    }

    override suspend fun getAllPreferencesEntries(): List<PrefData> = scope.async {
        preferencesDao.getAllEntities().map { it.toPrefData() }
    }.await()

    override fun clear() {
        scope.launch {
            preferencesDao.clearAll()
        }
    }
}
