package com.sandymist.android.debuglib.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.sandymist.android.debuglib.ui.screens.DebugScreen
import com.sandymist.android.debuglib.ui.ui.theme.DebugLibTheme
import com.sandymist.android.debuglib.ui.viewmodel.LogcatViewModel
import com.sandymist.android.debuglib.ui.viewmodel.NetworkLogViewModel
import com.sandymist.android.debuglib.ui.viewmodel.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DebugActivity : ComponentActivity() {
    private lateinit var logcatViewModel: LogcatViewModel
    private lateinit var preferencesViewModel: PreferencesViewModel
    private lateinit var networkLogViewModel: NetworkLogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DebugLibTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DebugScreen(
                        modifier = Modifier.padding(innerPadding),
                        logcatViewModel = logcatViewModel,
                        preferencesViewModel = preferencesViewModel,
                        networkLogViewModel = networkLogViewModel,
                    )
                }
            }
        }
    }
}
