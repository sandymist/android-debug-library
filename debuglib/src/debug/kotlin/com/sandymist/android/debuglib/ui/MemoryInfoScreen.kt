package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sandymist.android.common.utilities.getMemoryUsage

@Composable
fun MemoryInfoScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val memoryInfo by remember { mutableStateOf(getMemoryUsage(context)) }
    val tabs = listOf("bytes", "KB", "MB")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val factor by remember {
        derivedStateOf {
            when (selectedTabIndex) {
                0 -> 1
                1 -> 1000
                2 -> 1000000
                else -> 1
            }
        }
    }
    val units by remember {
        derivedStateOf {
            when (selectedTabIndex) {
                0 -> "bytes"
                1 -> "KB"
                2 -> "MB"
                else -> "bytes"
            }
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = CenterHorizontally,
    ) {
        Text("Memory Info", style = MaterialTheme.typography.titleLarge)

        TabRow(
            modifier = Modifier.padding(vertical = 10.dp),
            selectedTabIndex = selectedTabIndex
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(tab) }
                )
            }
        }

        DataItem(label = "Dirty memory: ${memoryInfo.dirtyMemoryInBytes / factor} $units")
        DataItem(label = "Clean memory: ${memoryInfo.cleanMemoryInBytes / factor} $units")
        DataItem(label = "Native heap size: ${memoryInfo.nativeHeapSizeInBytes / factor} $units")
        DataItem(label = "Available memory: ${memoryInfo.availableMemoryInBytes / factor} $units")
        DataItem(label = "Total memory: ${memoryInfo.totalMemoryInBytes / factor} $units")
    }
}
