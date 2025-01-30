package com.sandymist.android.debuglib.ui

import android.content.ContentUris
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
import com.sandymist.android.debuglib.model.HarData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File

@Composable
fun DebugMenu(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val networkLogViewModel = DebugLib.networkLogViewModel
    val logcatViewModel = DebugLib.logcatViewModel
    val preferencesViewModel = DebugLib.preferencesViewModel

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
        DataItem(label = "Summary", modifier = Modifier.debouncedClickable {
            navController.navigate("summary")
        })
    }
}

private const val mimeType = "application/octet-stream"

private fun shareDebugInfoAsFileAttachment(context: Context, combined: String, har: String) {
    val combinedFile = "debug_info.json"
    val combinedUri = saveFileToMediaStore(context, combined, combinedFile)
    val harFile = "network.har"
    val harUri = saveFileToMediaStore(context, har, harFile)

    if (combinedUri == null || harUri == null) {
        Timber.e("Failed to create the files for sharing")
        return
    }

    val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = mimeType
        putExtra(Intent.EXTRA_SUBJECT, "Debug Info")
        putExtra(Intent.EXTRA_TEXT, "Please find the debug info attached.")
        putParcelableArrayListExtra(
            Intent.EXTRA_STREAM,
            ArrayList(listOf(combinedUri, harUri))
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant read permission
    }

    context.startActivity(Intent.createChooser(sendIntent, "Send Debug Info"))
}

private fun saveFileToMediaStore(context: Context, data: String, fileName: String): Uri? {
    val contentResolver = context.contentResolver

    val targetPath = Environment.DIRECTORY_DOCUMENTS

    val existingFileUri = contentResolver.query(
        MediaStore.Files.getContentUri("external"),
        arrayOf(MediaStore.Files.FileColumns._ID),
        "${MediaStore.Files.FileColumns.DISPLAY_NAME} = ? AND ${MediaStore.Files.FileColumns.RELATIVE_PATH} = ?",
        arrayOf(fileName, targetPath),
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
            ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
        } else {
            null
        }
    }

    existingFileUri?.let {
        contentResolver.delete(it, null, null)
    }

    // Fallback: Delete directly from the file system if the file still exists
    val fallbackFile = File(Environment.getExternalStoragePublicDirectory(targetPath), fileName)
    if (fallbackFile.exists()) {
        fallbackFile.delete()
    }

    // Create a new file
    val contentValues = ContentValues().apply {
        put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
        put(MediaStore.Files.FileColumns.MIME_TYPE, mimeType)
        put(MediaStore.Files.FileColumns.RELATIVE_PATH, targetPath)
    }

    val contentUri = contentResolver.insert(
        MediaStore.Files.getContentUri("external"),
        contentValues
    )

    return if (contentUri != null) {
        try {
            contentResolver.openOutputStream(contentUri)?.use { outputStream ->
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
