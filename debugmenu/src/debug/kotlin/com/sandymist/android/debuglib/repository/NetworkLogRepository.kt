package com.sandymist.android.debuglib.repository

//import com.sandymist.android.debuglib.db.NetworkLogDao
//import com.sandymist.android.debuglib.db.NetworkLogEntity
import com.sandymist.android.debuglib.model.HarEntry
//import com.sandymist.android.debuglib.model.NetworkLog
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.toFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkLogRepository(
    boxStore: BoxStore,
//    private val networkLogDao: NetworkLogDao,
) {
    private val _networkLogList = MutableStateFlow<List<HarEntry>>(emptyList())
    val networkLogList = _networkLogList.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val networkLogBoxStore: Box<HarEntry> = boxStore.boxFor(HarEntry::class.java)
    private val query = networkLogBoxStore.query()
        .build()

    init {
        scope.launch {
            val flow = query.subscribe().toFlow()
            flow.collectLatest {
                _networkLogList.emit(it)
            }

//            networkLogDao.getAll().collectLatest {
//                _networkLogList.emit(it.map { entity -> entity.toNetworkLog() })
//            }
        }
    }

    suspend fun getAllNetworkLogEntries(): List<HarEntry> = scope.async {
        networkLogBoxStore.all//.map { it.toNetworkLog() }
//        networkLogDao.getAllEntities().map { it.toNetworkLog() }
    }.await()

    suspend fun getNetworkLog(id: Long): HarEntry {
        return scope.async {
            networkLogBoxStore.get(id)//.toNetworkLog()
//            networkLogDao.getNetworkLog(id).toNetworkLog()
        }.await()
    }

//    fun insert(networkLog: NetworkLog) = networkLogBoxStore.put(NetworkLogEntity.fromNetworkLog(networkLog))
    fun insert(harEntry: HarEntry) = networkLogBoxStore.put(harEntry)

    fun clear() {
        scope.launch {
            networkLogBoxStore.removeAll()
//            networkLogDao.clearAll()
        }
    }
}
