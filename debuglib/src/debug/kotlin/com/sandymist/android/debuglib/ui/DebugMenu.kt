package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sandymist.android.common.utilities.debouncedClickable
import com.sandymist.android.debuglib.model.ExportData
import com.sandymist.android.debuglib.model.HarData
import com.sandymist.android.debuglib.ui.component.DataItem
import com.sandymist.android.debuglib.ui.viewmodel.LogcatViewModel
import com.sandymist.android.debuglib.ui.viewmodel.NetworkLogViewModel
import com.sandymist.android.debuglib.ui.viewmodel.PreferencesViewModel
import com.sandymist.android.debuglib.utils.shareDebugInfoAsFileAttachment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@Composable
fun DebugMenu(
    modifier: Modifier = Modifier,
    navController: NavController,
    logcatViewModel: LogcatViewModel,
    preferencesViewModel: PreferencesViewModel,
    networkLogViewModel: NetworkLogViewModel,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val menuList = listOf(
        MenuItem(label = "Network log", action = { navController.navigate("network-log") }),
        MenuItem(label = "Logcat", action = { navController.navigate("logcat") }),
        MenuItem(label = "View preferences", action = { navController.navigate("preferences") }),
        MenuItem(label = "Room", action = { navController.navigate("room") }),
        MenuItem(label = "Device monitor", action = { navController.navigate("device-monitor") }),
        MenuItem(label = "Memory info", action = { navController.navigate("memory-info") }),
//        MenuItem(label = "View DataStore", action = { navController.navigate("datastore") }),
//        MenuItem(label = "Build info", action = { navController.navigate("build-info") }),
    )

    Column(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Debug screen", style = MaterialTheme.typography.headlineMedium)
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Export",
                modifier = Modifier
                    .debouncedClickable {
                        scope.launch(Dispatchers.Default) {
                            val networkLogData = networkLogViewModel.getNetworkLogEntries()
                            val logcatData = logcatViewModel.getAllLogcatEntries()
                            val preferencesData = preferencesViewModel.getAllPreferenceEntries()
                            val exportData = ExportData(
                                networkLogList = networkLogData,
                                logcatList = logcatData,
                                preferencesList = preferencesData,
                            )
                            val dataAsString =
                                Json.encodeToString(ExportData.serializer(), exportData)

                            val harData = HarData(
                                log = HarData.Log(
                                    creator = HarData.Log.Creator(name = "DebugLib", version = "0.1"),
                                    entries = networkLogData,
                                    pages = null,
                                    version = "1.0",
                                )
                            )
                            val harAsString = Json.encodeToString(HarData.serializer(), harData)
                            withContext(Dispatchers.Main) {
                                shareDebugInfoAsFileAttachment(context, dataAsString, harAsString)
                            }
                        }
                    }
            )
        }
        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))

        MenuRow(menuItems = menuList)
    }
}

data class MenuItem(val label: String, val action: () -> Unit)

@Composable
fun MenuRow(menuItems: List<MenuItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(menuItems.size) { index ->
            val menuItem = menuItems[index]
            DataItem(label = menuItem.label, modifier = Modifier.debouncedClickable {
                menuItem.action()
            })
            HorizontalDivider(color = Color.LightGray)
        }
    }
}
