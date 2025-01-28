package com.sandymist.android.debuglib

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.sandymist.android.debuglib.db.DebugLibDatabase
import com.sandymist.android.debuglib.db.LogcatDao
import com.sandymist.android.debuglib.db.LogcatEntity
import com.sandymist.android.debuglib.db.NetworkLogDao
import com.sandymist.android.debuglib.db.NetworkLogEntity
import com.sandymist.android.debuglib.model.Logcat
import com.sandymist.android.debuglib.model.NetworkLog
import com.sandymist.android.debuglib.repository.LogcatRepository
import com.sandymist.android.debuglib.repository.NetworkLogRepository
import com.sandymist.android.debuglib.ui.viewmodel.LogcatViewModel
import com.sandymist.android.debuglib.ui.viewmodel.LogcatViewModelFactory
import com.sandymist.android.debuglib.ui.viewmodel.NetworkLogViewModel
import com.sandymist.android.debuglib.ui.viewmodel.NetworkLogViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

object DebugLib {
    private val networkLogFlow = MutableStateFlow<NetworkLog?>(null)
    private val logcatFlow = MutableStateFlow<Logcat?>(null)
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var database: DebugLibDatabase
    private lateinit var networkLogDao: NetworkLogDao
    private lateinit var logcatDao: LogcatDao
    private lateinit var networkLogRepository: NetworkLogRepository
    private lateinit var logcatRepository: LogcatRepository
    lateinit var networkLogViewModel: NetworkLogViewModel
    lateinit var logcatViewModel: LogcatViewModel
    private val viewModelStore = ViewModelStore()

    @Suppress("unused")
    fun init(context: Context) {
        database = DebugLibDatabase.getDatabase(context)
        networkLogDao = database.networkLogDao()
        logcatDao = database.logcatDao()
        networkLogRepository = NetworkLogRepository(networkLogDao)
        logcatRepository = LogcatRepository(logcatDao)

        val networkLogViewModelFactory = NetworkLogViewModelFactory(networkLogRepository)
        networkLogViewModel = ViewModelProvider(viewModelStore, networkLogViewModelFactory)[NetworkLogViewModel::class.java]

        val logcatViewModelFactory = LogcatViewModelFactory(logcatRepository)
        logcatViewModel = ViewModelProvider(viewModelStore, logcatViewModelFactory)[LogcatViewModel::class.java]

        scope.launch {
            networkLogFlow.collect { networkLog ->
                networkLog?.let {
                    networkLogDao.insert(NetworkLogEntity.fromNetworkLog(networkLog))
                }
            }
        }

        scope.launch {
            logcatFlow.collect { logcat ->
                logcat?.let {
                    logcatDao.insert(LogcatEntity.fromLogcat(logcat))
                }
            }
        }
    }

    fun insertNetworkLog(networkLog: NetworkLog) {
        scope.launch {
            networkLogFlow.emit(networkLog)
        }
    }

    fun getRoomDatabaseSize(context: Context) = DebugLibDatabase.getDatabaseSize(context)

    @Suppress("unused")
    fun shutdown() {
        viewModelStore.clear()
        scope.cancel()
    }
}
