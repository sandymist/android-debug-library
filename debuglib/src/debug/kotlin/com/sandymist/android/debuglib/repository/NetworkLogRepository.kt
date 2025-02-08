package com.sandymist.android.debuglib.repository

import com.sandymist.android.debuglib.model.HarEntry
import com.sandymist.android.debuglib.model.HarEntry_
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.toFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface NetworkLogRepository {
    val networkLogList: StateFlow<List<HarEntry>>

    suspend fun getAllNetworkLogEntries(): List<HarEntry>
    suspend fun getNetworkLog(id: Long): HarEntry
    fun insert(harEntry: HarEntry): Long
    fun clear()
}

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class NetworkLogRepositoryImpl @Inject constructor(
    boxStore: BoxStore,
): NetworkLogRepository {
    private val _networkLogList = MutableStateFlow<List<HarEntry>>(emptyList())
    override val networkLogList = _networkLogList.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val networkLogBoxStore: Box<HarEntry> = boxStore.boxFor(HarEntry::class.java)
    private val query = networkLogBoxStore.query()
        .orderDesc(HarEntry_.createdAt)
        .build()

    init {
        scope.launch {
            val flow = query
                .subscribe()
                .toFlow()
            flow.collectLatest {
                _networkLogList.emit(it)
            }
        }

        scope.launch {
            query.subscribe().onlyChanges().observer {
                val count = networkLogBoxStore.count()
                if (count > HIGH_WATER_MARK) {
                    deleteOldestEntries(count - LOW_WATER_MARK)
                }
            }
        }
    }

    override suspend fun getAllNetworkLogEntries(): List<HarEntry> = scope.async {
        networkLogBoxStore.all
    }.await()

    override suspend fun getNetworkLog(id: Long): HarEntry {
        return scope.async {
            networkLogBoxStore.get(id)
        }.await()
    }

    override fun insert(harEntry: HarEntry): Long = networkLogBoxStore.put(harEntry)

    override fun clear() {
        scope.launch {
            networkLogBoxStore.removeAll()
        }
    }

    private fun deleteOldestEntries(deleteCount: Long) {
        if (deleteCount <= 0) return

        val oldEntries = networkLogBoxStore.query()
            .order(HarEntry_.createdAt) // Order by oldest first
            .build()
            .find(0, deleteCount)

        networkLogBoxStore.remove(oldEntries)
    }

    companion object {
        private const val HIGH_WATER_MARK = 1000
        private const val LOW_WATER_MARK = 800
    }
}
