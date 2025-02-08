package com.sandymist.android.debuglib.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sandymist.android.debuglib.ui.DebugMenu
import com.sandymist.android.debuglib.ui.viewmodel.LogcatViewModel
import com.sandymist.android.debuglib.ui.viewmodel.NetworkLogViewModel
import com.sandymist.android.debuglib.ui.viewmodel.PreferencesViewModel
import com.sandymist.android.debuglib.ui.viewmodel.RoomDBViewModel
import com.sandymist.android.devicemonitor.MonitorScreen

@Composable
fun DebugScreen(
    modifier: Modifier = Modifier,
    logcatViewModel: LogcatViewModel = hiltViewModel(),
    preferencesViewModel: PreferencesViewModel = hiltViewModel(),
    networkLogViewModel: NetworkLogViewModel = hiltViewModel(),
    roomDBViewModel: RoomDBViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "debug-menu") {
        composable("debug-menu") {
            DebugMenu(
                modifier = modifier,
                navController = navController,
                logcatViewModel = logcatViewModel,
                preferencesViewModel = preferencesViewModel,
                networkLogViewModel = networkLogViewModel,
            )
        }
        composable("logcat") {
            LogcatScreen(
                modifier = modifier,
                logcatViewModel = logcatViewModel,
            )
        }
        composable("network-log") {
            NetworkLogScreen(
                modifier = modifier,
                networkLogViewModel = networkLogViewModel,
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
                    val networkLog = networkLogViewModel.getNetworkLog(id)
                    networkLog
                },
            )
        }
        composable("preferences") {
            PreferencesScreen(
                modifier = modifier,
                preferencesViewModel = preferencesViewModel,
            )
        }
        composable("datastore") {
            DataStoreScreen(modifier = modifier)
        }
        composable("room") {
            RoomScreen(
                modifier = modifier,
                getDatabaseSize = { roomDBViewModel.getRoomDatabaseSize(context) },
                getDatabasesAndTables = { roomDBViewModel.getRoomDatabasesAndTables(context) },
            )
        }
        composable("device-monitor") {
            MonitorScreen(
                modifier = modifier,
            )
        }
        composable("memory-info") {
            MemoryInfoScreen(modifier = modifier)
        }
        composable("build-info") {
            BranchInfoScreen(modifier = modifier)
        }

    }
}
