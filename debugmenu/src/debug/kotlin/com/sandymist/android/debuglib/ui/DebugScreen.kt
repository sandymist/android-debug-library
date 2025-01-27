package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sandymist.android.common.utilities.debouncedClickable
import com.sandymist.android.debuglib.DebugLib

@Composable
fun DebugScreen(
     modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "debug-menu") {
        composable("debug-menu") {
            DebugMenu(modifier = modifier, navController = navController)
        }
        composable("logcat") {
            LogcatScreen(modifier = modifier)
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
                navArgument("id") { type = NavType.StringType },
            )
        ) {
            val id = it.arguments?.getString("id") ?: ""

            NetworkLogDetailScreen(
                modifier = modifier,
                getNetworkLog = {
                    val networkLog = DebugLib.networkLogViewModel.getNetworkLog(id)
                    networkLog
                },
            )
        }
        composable("preferences") {
            PreferencesScreen(modifier = modifier)
        }
        composable("datastore") {
            DataStoreScreen(modifier = modifier)
        }
    }
}

@Composable
fun DebugMenu(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .padding(horizontal =  12.dp, vertical = 10.dp)
    ) {
        Text("Debug screen", style = MaterialTheme.typography.headlineSmall)
        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))

        DataItem( label = "Network log", modifier = Modifier.debouncedClickable {
            navController.navigate("network-log")
        })
        DataItem( label = "Logcat", modifier = Modifier.debouncedClickable {
            navController.navigate("logcat")
        })
        DataItem( label = "View preferences", modifier = Modifier.debouncedClickable {
            navController.navigate("preferences")
        })
        DataItem( label = "View DataStore", modifier = Modifier.debouncedClickable {
            navController.navigate("datastore")
        })
    }
}
