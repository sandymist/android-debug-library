package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sandymist.android.debuglib.LogcatListener

@Composable
fun LogcatScreen(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val logList = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        LogcatListener(
            log = { log ->
                logList.add(0, log)
            },
            scope = scope
        )
    }

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 12.dp),
    ) {
        item {
            Header(
                title = "Logcat",
                actionHandler = ActionHandler.deleteHandler { logList.clear() },
            )
        }

        items(logList) {
            Text(
                text = it,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
            )
            HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
