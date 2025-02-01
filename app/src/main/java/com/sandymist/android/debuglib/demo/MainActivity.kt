package com.sandymist.android.debuglib.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.sandymist.android.debuglib.demo.theme.DebugLibDemoTheme
import com.sandymist.android.debuglib.ui.DebugScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.IOException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        generateTraffic()

        setContent {
            DebugLibDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DebugScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    private var counter = 100
    private fun generateTraffic() {
        lifecycleScope.launch(Dispatchers.IO) {
            while (--counter >= 0) {
                try {
                    val response = performGetRequest()
                    Timber.d(response)
                } catch (e: Exception) {
                    Timber.e("Error: ${e.message}")
                    "Exception: ${e.javaClass.name} - ${e.message}"
                }
                delay(5000L)
            }
        }
    }

    private fun performGetRequest(): String {
        val url = "https://jsonplaceholder.typicode.com/posts/1"
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body?.string() ?: throw IOException("Empty response body")
        }
    }
}
