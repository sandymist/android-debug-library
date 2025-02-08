package com.sandymist.android.debuglib.repository

import com.sandymist.android.debuglib.db.LogcatDao
import com.sandymist.android.debuglib.db.LogcatEntity
import com.sandymist.android.debuglib.model.Logcat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface LogcatRepository {
    val logcatList: StateFlow<List<Logcat>>
    suspend fun getAllLogcatEntries(): List<Logcat>
    suspend fun insertLogcat(logcat: Logcat)
    fun clear()
}

@Singleton
class LogcatRepositoryImpl @Inject constructor(
    private val logcatDao: LogcatDao,
): LogcatRepository {
    private val _logcatList = MutableStateFlow<List<Logcat>>(emptyList())
    override val logcatList = _logcatList.asStateFlow()
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
                    insertLogcat(logcat)
                }
            },
            scope = scope
        )
    }

    override suspend fun getAllLogcatEntries(): List<Logcat> = scope.async {
        logcatDao.getAllEntities().map { it.toLogcat() }
    }.await()

    override suspend fun insertLogcat(logcat: Logcat) {
        scope.launch {
            logcatDao.insert(LogcatEntity.fromLogcat(logcat))
        }
    }

    override fun clear() {
        scope.launch {
            logcatDao.clearAll()
        }
    }
}
