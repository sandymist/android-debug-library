@file:Suppress("DEPRECATION")
package com.sandymist.android.debuglib.ui

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Suppress("unused")
@Composable
fun PreferencesScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val items = remember { mutableStateListOf<PrefItem>() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val defaultPrefs = sharedPreferences.all
            if (defaultPrefs.isNotEmpty()) {
                items.add(PrefItem.Header("Default"))
            }
            val sharedPrefsDir = File(context.filesDir.parent, "shared_prefs")
            if (sharedPrefsDir.exists() && sharedPrefsDir.isDirectory) {
                val sharedPrefsFiles = sharedPrefsDir.listFiles()
                sharedPrefsFiles?.forEach { file ->
                    val prefsName = file.nameWithoutExtension
                    items.add(PrefItem.Header(prefsName))
                    val sharedPrefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                    val allEntries = sharedPrefs.all
                    for ((key, value) in allEntries) {
                        items.add(PrefItem.Data(key, value?.toString() ?: "Not set"))
                    }
                }
            }

            val dataStoreDir = File(context.filesDir, "datastore")
            if (dataStoreDir.exists() && dataStoreDir.isDirectory) {
                val dataStoreFiles = dataStoreDir.listFiles()
                dataStoreFiles?.forEach { file ->
                    println("DataStore file: ${file.name}")
                }
            }
        }
    }

    Column(
        modifier = modifier
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Header(title = "Preferences")

        if (items.isEmpty()) {
            Box(
                modifier = Modifier.weight(0.1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "No preferences found",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            PreferencesList(items)
        }
    }
}

@Composable
fun PreferencesList(preferences: List<PrefItem>) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        items(preferences) { item ->
            when (item) {
                is PrefItem.Header -> {
                    Text(item.title, style = MaterialTheme.typography.headlineSmall)
                    HorizontalDivider(color = Color.DarkGray)
                }
                is PrefItem.Data -> {
                    DataItem(label = "${item.key}: ${item.value}")
//                    HorizontalDivider(color = Color.LightGray)
                }
            }
        }
    }
}

sealed interface PrefItem {
    data class Header(val title: String): PrefItem
    data class Data(val key: String, val value: String): PrefItem
}
