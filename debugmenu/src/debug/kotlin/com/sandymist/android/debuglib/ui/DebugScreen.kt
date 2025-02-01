package com.sandymist.android.debuglib.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sandymist.android.debuglib.DebugLib
import com.sandymist.android.debuglib.devicemonitor.DeviceMonitor
import com.sandymist.android.debuglib.devicemonitor.MonitorScreen
import com.sandymist.android.debuglib.devicemonitor.audio.AudioMonitor
import com.sandymist.android.debuglib.devicemonitor.network.NetworkMonitor
import com.sandymist.android.debuglib.devicemonitor.power.PowerMonitor

@Composable
fun DebugScreen(
     modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "debug-menu") {
        composable("debug-menu") {
            DebugMenu(modifier = modifier, navController = navController)
        }
        composable("logcat") {
            LogcatScreen(
                modifier = modifier,
                logcatViewModel = DebugLib.logcatViewModel,
            )
        }
        composable("network-log") {
            NetworkLogScreen(
                modifier = modifier,
                networkLogViewModel = DebugLib.networkLogViewModel,
                onClick = { id ->
                    navController.navigate("network-log-detail/${id}")
                }
            )
        }
        composable(
            route = "network-log-detail/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.LongType },
            )
        ) {
            val id = it.arguments?.getLong("id") ?: -1

            NetworkLogDetailScreen(
                modifier = modifier,
                getNetworkLog = {
                    val networkLog = DebugLib.networkLogViewModel.getNetworkLog(id)
                    networkLog
                },
            )
        }
        composable("preferences") {
            PreferencesScreen(
                modifier = modifier,
                preferencesViewModel = DebugLib.preferencesViewModel,
            )
        }
        composable("datastore") {
            DataStoreScreen(modifier = modifier)
        }
        composable("room") {
            RoomScreen(modifier = modifier)
        }
        composable("device-monitor") {
            val networkMonitor by lazy { NetworkMonitor.getInstance(context) }
            val powerMonitor by lazy { PowerMonitor.getInstance(context) }
            val audioMonitor by lazy { AudioMonitor.getInstance(context) }
            val deviceMonitor by lazy { DeviceMonitor(context) }

            MonitorScreen(
                modifier = modifier,
                networkMonitor = networkMonitor,
                powerMonitor = powerMonitor,
                audioMonitor = audioMonitor,
                deviceMonitor = deviceMonitor,
            )
        }
    }
}
