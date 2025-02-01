package com.sandymist.android.debuglib

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.sandymist.android.debuglib.db.DebugLibDatabase
import com.sandymist.android.debuglib.db.LogcatDao
import com.sandymist.android.debuglib.db.PreferencesDao
import com.sandymist.android.debuglib.model.HarEntry
import com.sandymist.android.debuglib.model.Logcat
import com.sandymist.android.debuglib.model.MyObjectBox
import com.sandymist.android.debuglib.repository.LogcatRepository
import com.sandymist.android.debuglib.repository.NetworkLogRepository
import com.sandymist.android.debuglib.repository.PreferencesRepository
import com.sandymist.android.debuglib.ui.viewmodel.LogcatViewModel
import com.sandymist.android.debuglib.ui.viewmodel.LogcatViewModelFactory
import com.sandymist.android.debuglib.ui.viewmodel.NetworkLogViewModel
import com.sandymist.android.debuglib.ui.viewmodel.NetworkLogViewModelFactory
import com.sandymist.android.debuglib.ui.viewmodel.PreferencesViewModel
import com.sandymist.android.debuglib.ui.viewmodel.PreferencesViewModelFactory
import io.objectbox.BoxStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object DebugLib {
    private val networkLogFlow = MutableStateFlow<HarEntry?>(null)
    private val logcatFlow = MutableStateFlow<Logcat?>(null)
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var database: DebugLibDatabase
    private lateinit var logcatDao: LogcatDao
    private lateinit var preferencesDao: PreferencesDao
    private lateinit var networkLogRepository: NetworkLogRepository
    private lateinit var logcatRepository: LogcatRepository
    private lateinit var preferencesRepository: PreferencesRepository
    lateinit var networkLogViewModel: NetworkLogViewModel
    lateinit var logcatViewModel: LogcatViewModel
    lateinit var preferencesViewModel: PreferencesViewModel
    private val viewModelStore = ViewModelStore()
    private lateinit var boxStore: BoxStore

    @Suppress("unused")
    fun init(context: Context) {
        database = DebugLibDatabase.getDatabase(context)
        logcatDao = database.logcatDao()
        preferencesDao = database.preferencesDao()
        boxStore = MyObjectBox.builder()
            .androidContext(context)
            .build()
        networkLogRepository = NetworkLogRepository(boxStore)
        logcatRepository = LogcatRepository(logcatDao)
        preferencesRepository = PreferencesRepository(context, preferencesDao)

        val networkLogViewModelFactory = NetworkLogViewModelFactory(networkLogRepository)
        networkLogViewModel = ViewModelProvider(viewModelStore, networkLogViewModelFactory)[NetworkLogViewModel::class.java]

        val logcatViewModelFactory = LogcatViewModelFactory(logcatRepository)
        logcatViewModel = ViewModelProvider(viewModelStore, logcatViewModelFactory)[LogcatViewModel::class.java]

        val preferencesViewModelFactory = PreferencesViewModelFactory(preferencesRepository)
        preferencesViewModel = ViewModelProvider(viewModelStore, preferencesViewModelFactory)[PreferencesViewModel::class.java]

        scope.launch {
            networkLogFlow.collect { networkLog ->
                networkLog?.let {
                    networkLogRepository.insert(networkLog)
                }
            }
        }

        scope.launch {
            logcatFlow.collect { logcat ->
                logcat?.let {
                    logcatRepository.insertLogcat(logcat)
                }
            }
        }
    }

    fun insertNetworkLog(harEntry: HarEntry) {
        scope.launch {
            networkLogFlow.emit(harEntry)
        }
    }

    suspend fun getRoomDatabaseSize(context: Context) = withContext(Dispatchers.IO) {
        DebugLibDatabase.getDatabaseSize(context)
    }

    suspend fun getRoomDatabasesAndTables(context: Context): Map<String, List<String>> = withContext(Dispatchers.IO) {
        val databasesPath = context.getDatabasePath("dummy").parentFile
        val allTables = mutableMapOf<String, List<String>>()

        if (databasesPath?.exists() == true) {
            databasesPath.list()?.take(1)?.forEach { dbName ->
                val dbFile = File(databasesPath, dbName)

                // Filter for Room database files (usually ends with .db)
                if (dbFile.exists() && dbFile.isFile /* && dbName.endsWith(".db") */) {
                    val database = SQLiteDatabase.openDatabase(dbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
                    val tables = getTablesFromDatabase(database)
                    database.close()
                    allTables[dbName] = tables
                }
            }
        }

        allTables
    }

    private fun getTablesFromDatabase(database: SQLiteDatabase): List<String> {
        val tableNames = mutableListOf<String>()
        val cursor: Cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        cursor.use {
            while (it.moveToNext()) {
                tableNames.add(it.getString(0))
            }
        }
        return tableNames
    }

    @Suppress("unused")
    fun shutdown() {
        viewModelStore.clear()
        scope.cancel()
    }
}
