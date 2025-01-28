package com.sandymist.android.debuglib.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.sandymist.android.debuglib.DebugLib
import com.sandymist.android.debuglib.model.ExportData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber

@Composable
fun DebugMenu(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val networkLogViewModel = DebugLib.networkLogViewModel
    val logcatViewModel = DebugLib.logcatViewModel

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
                imageVector = Icons.Filled.KeyboardArrowUp, // TODO: change icon
                contentDescription = "Export",
                modifier = Modifier
                    .debouncedClickable {
                        scope.launch(Dispatchers.Default) {
                            val networkLogData = networkLogViewModel.getAllNetworkLogEntries()
                            val logcatData = logcatViewModel.getAllLogcatEntries()
                            val exportData = ExportData(
                                networkLogList = networkLogData,
                                logcatList = logcatData,
                            )
                            val dataAsString =
                                Json.encodeToString(ExportData.serializer(), exportData)
                            withContext(Dispatchers.Main) {
                                shareDebugInfoAsFileAttachment(context, dataAsString)
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
        DataItem(label = "View DataStore", modifier = Modifier.debouncedClickable {
            navController.navigate("datastore")
        })
        DataItem(label = "Summary", modifier = Modifier.debouncedClickable {
            navController.navigate("summary")
        })
    }
}

private fun shareDebugInfoAsFileAttachment(context: Context, data: String, fileName: String = "debug_info.json") {
    saveFileToMediaStore(context, data, fileName)?.let { contentUri ->
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_SUBJECT, "Debug Info")
            putExtra(Intent.EXTRA_TEXT, "Please find the debug info attached.")
            putExtra(Intent.EXTRA_STREAM, contentUri) // Use the MediaStore-generated URI
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant read permission
        }

        context.startActivity(Intent.createChooser(sendIntent, "Send Debug Info"))
    } ?: run {
        Timber.e("Failed to prepare the file for sharing")
    }

    // TODO: call this only after file share is complete
    // context.contentResolver.delete(contentUri, null, null)
}

private fun saveFileToMediaStore(context: Context, data: String, fileName: String): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
        put(MediaStore.Files.FileColumns.MIME_TYPE, "application/json")
        put(/* key = */ MediaStore.Files.FileColumns.RELATIVE_PATH, /* value = */ Environment.DIRECTORY_DOCUMENTS) // Target path
    }

    val contentUri = context.contentResolver.insert(
        MediaStore.Files.getContentUri("external"),
        contentValues
    )

    return if (contentUri != null) {
        try {
            context.contentResolver.openOutputStream(contentUri)?.use { outputStream ->
                outputStream.write(data.toByteArray(Charsets.UTF_8))
            }
            contentUri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } else {
        null
    }
}
