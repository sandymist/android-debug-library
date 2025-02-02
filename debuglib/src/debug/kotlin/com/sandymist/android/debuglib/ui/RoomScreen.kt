package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sandymist.android.debuglib.DebugLib
import com.sandymist.android.debuglib.model.DataListItem

@Composable
fun RoomScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext
    val dbSize = remember { mutableLongStateOf(0L) }
    val tables = remember { mutableStateOf<Map<String, List<String>>?>(null) }
    val tableList = remember { derivedStateOf {
        sequence {
            tables.value?.entries?.forEach {
                yield(DataListItem.Header("Table: ${it.key}"))
                yieldAll(it.value.map { value -> DataListItem.Data(key = value) })
            }
        }
    }}

    LaunchedEffect(Unit) {
        dbSize.longValue = DebugLib.getRoomDatabaseSize(context)
        tables.value = DebugLib.getRoomDatabasesAndTables(context)
    }

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 12.dp),
    ) {
        item {
            Header(title = "Room")
        }

        item {
            NameValueItem(
                name = "Room DB size",
                value = "${dbSize.longValue} bytes"
            )
        }

        items(tableList.value.toList()) {
            when (it) {
                is DataListItem.Header -> {
                    Text(
                        it.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 6.dp),
                    )
                }
                is DataListItem.Data -> {
                    DataItem(label = it.key, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}
