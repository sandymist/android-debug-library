package com.sandymist.android.debuglib.repository

import com.sandymist.android.debuglib.LogcatListener
import com.sandymist.android.debuglib.db.LogcatDao
import com.sandymist.android.debuglib.db.LogcatEntity
import com.sandymist.android.debuglib.model.Logcat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LogcatRepository(
    private val logcatDao: LogcatDao,
) {
    private val _logcatList = MutableStateFlow<List<Logcat>>(emptyList())
    val logcatList = _logcatList.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            logcatDao.getAll().collectLatest {
                _logcatList.emit(it.map { entity -> entity.toLogcat() })
            }
        }

        LogcatListener(
            log = { log ->
                scope.launch {
                    val logcat = Logcat(message = log)
                    logcatDao.insert(LogcatEntity.fromLogcat(logcat))
                }
            },
            scope = scope
        )
    }

    suspend fun getAllLogcatEntries(): List<Logcat> = scope.async {
        logcatDao.getAllEntities().map { it.toLogcat() }
    }.await()

    fun clear() {
        scope.launch {
            logcatDao.clearAll()
        }
    }
}
