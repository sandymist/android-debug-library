package com.sandymist.android.debuglib.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sandymist.android.debuglib.model.DataListItem
import com.sandymist.android.debuglib.ui.component.DataItem
import com.sandymist.android.debuglib.ui.component.Header
import com.sandymist.android.debuglib.ui.component.NameValueItem

@Composable
fun RoomScreen(
    modifier: Modifier = Modifier,
    getDatabaseSize: suspend () -> Long,
    getDatabasesAndTables: suspend () -> Map<String, List<String>>,
) {
    val dbSize = remember { mutableLongStateOf(0L) }
    val tables = remember { mutableStateOf<Map<String, List<String>>?>(null) }
    val tableList = remember { derivedStateOf {
        sequence {
            tables.value?.entries?.forEach {
                yield(DataListItem.Header("Table ${it.key}"))
                yieldAll(it.value.map { value -> DataListItem.Data(key = value) })
            }
        }
    }}

    LaunchedEffect(Unit) {
        dbSize.longValue = getDatabaseSize()
        tables.value = getDatabasesAndTables()
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
                value = "${dbSize.longValue} bytes",
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        item {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }

        items(tableList.value.toList()) {
            when (it) {
                is DataListItem.Header -> {
                    Text(
                        it.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 6.dp),
                    )
                }
                is DataListItem.Data -> {
                    DataItem(label = it.key, modifier = Modifier.padding(start = 8.dp))
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Preview
@Composable
fun PreviewRoomScreen() {
    RoomScreen(
        getDatabaseSize = { 123456678L },
        getDatabasesAndTables = {
            mapOf("app_db" to listOf("table1", "table2", "table3", "table4"))
        },
    )
}
