package com.sandymist.android.debuglib.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import android.os.Process
import android.util.Log
import kotlinx.coroutines.flow.catch
import timber.log.Timber

class LogcatListener(
    minLogLevel: Int = Log.INFO,
    private val log: (String) -> Unit,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val pid = Process.myPid()

    init {
        scope.launch {
            startListening()
        }
    }

    private val logcatCommand = arrayOf(
        "logcat",
        "-v", "time",
        "*:${minLogLevel}",
//        "*:S", // Suppress other log levels
        "--pid=$pid"
    )

    private fun listenForLogs(): Flow<String> = flow {
        val process = ProcessBuilder(*logcatCommand).start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        // read logcat output and emit it to the flow
        reader.use {
            while (true) {
                val logLine = it.readLine()
                if (logLine != null) {
                    emit(logLine)
                } else {
                    delay(100)
                }
            }
        }
    }
        .flowOn(Dispatchers.IO)

    private suspend fun startListening() {
        listenForLogs()
            .catch {
                Timber.e(it, "Exception in logcat flow: ${it.message}")
            }
            .collect { logMessage ->
                log(logMessage)
            }
    }
}
