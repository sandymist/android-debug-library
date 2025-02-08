package com.sandymist.android.debuglib.ui.screens

import android.content.Context
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
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.sandymist.android.debuglib.model.DataListItem
import com.sandymist.android.debuglib.ui.component.DataItem
import com.sandymist.android.debuglib.ui.component.Header
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import java.io.File

/*
 * WORK IN PROGRESS
 */

@Suppress("unused")
@Composable
fun DataStoreScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val items = remember { mutableStateListOf<DataListItem>() }

    LaunchedEffect(Unit) {
        readDataStore(context).collectLatest {
            items.add(DataListItem.Header(it.first))
            it.second.forEach { (t, u) ->
                items.add(DataListItem.Data(t, u.toString()))
            }
        }
    }

    Column(
        modifier = modifier
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Header(title = "Data Store")

        if (items.isEmpty()) {
            Box(
                modifier = Modifier.weight(0.1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "No datastore found",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            DataStoreList(items)
        }
    }
}

@Composable
fun DataStoreList(preferences: List<DataListItem>) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        items(preferences) { item ->
            when (item) {
                is DataListItem.Header -> {
                    Text(item.title, style = MaterialTheme.typography.headlineSmall)
                    HorizontalDivider(color = Color.DarkGray)
                }
                is DataListItem.Data -> {
                    DataItem(label = "${item.key}: ${item.value}")
                }
            }
        }
    }
}

fun readDataStore(
    context: Context,
): Flow<Pair<String, Map<String, Any>>> {
    val dataStoreDir = File(context.filesDir, "datastore")
    val dataStoreFiles = dataStoreDir.listFiles() /*?.filter { it.endsWith("pb") } ?: emptyList() */

    // THIS WON'T WORK - NEED TO AVOID MULTIPLE ACCESS TO DATASTORE
    return flow {
        dataStoreFiles?.forEach { file ->
            val fileName = file.nameWithoutExtension
            val dataStore = PreferenceDataStoreFactory.create {
                context.preferencesDataStoreFile(fileName)
            }

            dataStore.data.collectLatest { preferences ->
                val contents = preferences.asMap().mapKeys { entry ->
                    entry.key.name
                }.mapValues { entry ->
                    entry.value
                }
                emit(Pair(fileName, contents))
            }
        }
    }
}
