package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sandymist.android.debuglib.ui.viewmodel.LogcatViewModel

@Composable
fun LogcatScreen(
    modifier: Modifier = Modifier,
    logcatViewModel: LogcatViewModel,
) {
    val logList by logcatViewModel.logcatList.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 12.dp),
    ) {
        item {
            Header(
                title = "Logcat (${logList.size}) entries",
                actionHandler = ActionHandler.deleteHandler {
                    logcatViewModel.clear()
                },
            )
        }

        items(logList) {
            Text(
                text = it.message,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
            )
            HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
