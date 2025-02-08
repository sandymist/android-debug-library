package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

    Column(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Debug screen", style = MaterialTheme.typography.headlineSmall)
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Export",
                modifier = Modifier
                    .debouncedClickable {
                        scope.launch(Dispatchers.Default) {
                            val networkLogData = networkLogViewModel.getAllNetworkLogEntries()
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

        DataItem(label = "Network log", modifier = Modifier.debouncedClickable {
            navController.navigate("network-log")
        })
        DataItem(label = "Logcat", modifier = Modifier.debouncedClickable {
            navController.navigate("logcat")
        })
        DataItem(label = "View preferences", modifier = Modifier.debouncedClickable {
            navController.navigate("preferences")
        })
        // TODO: enable after figuring out how to handle not instantiating multiple data store
        // objects
//        DataItem(label = "View DataStore", modifier = Modifier.debouncedClickable {
//            navController.navigate("datastore")
//        })
        DataItem(label = "Room", modifier = Modifier.debouncedClickable {
            navController.navigate("room")
        })
        DataItem(label = "Device monitor", modifier = Modifier.debouncedClickable {
            navController.navigate("device-monitor")
        })
//        DataItem(label = "Build info", modifier = Modifier.debouncedClickable {
//            navController.navigate("build-info")
//        })
        DataItem(label = "Memory info", modifier = Modifier.debouncedClickable {
            navController.navigate("memory-info")
        })
    }
}
