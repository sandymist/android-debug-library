package com.sandymist.android.debuglib.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import timber.log.Timber
import java.io.File

private const val mimeType = "application/octet-stream"

fun shareDebugInfoAsFileAttachment(context: Context, combined: String, har: String) {
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
