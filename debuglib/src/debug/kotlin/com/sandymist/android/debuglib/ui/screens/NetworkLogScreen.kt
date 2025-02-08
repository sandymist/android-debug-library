package com.sandymist.android.debuglib.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sandymist.android.common.utilities.ageString
import com.sandymist.android.common.utilities.debouncedClickable
import com.sandymist.android.debuglib.model.HarEntry
import com.sandymist.android.debuglib.ui.component.ActionHandler
import com.sandymist.android.debuglib.ui.component.Header
//import com.sandymist.android.debuglib.model.NetworkLog
import com.sandymist.android.debuglib.ui.viewmodel.NetworkLogViewModel

@Suppress("unused")
@Composable
fun NetworkLogScreen(
    modifier: Modifier = Modifier,
    networkLogViewModel: NetworkLogViewModel,
    onClick: (Long) -> Unit,
) {
    val networkLog by networkLogViewModel.networkLogList.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Header(
            title = "Network log",
            actionHandler = ActionHandler.deleteHandler { networkLogViewModel.clear() },
        )

        if (networkLog.isEmpty()) {
            Box(
                modifier = Modifier.weight(0.1f),
                contentAlignment = Alignment.Center,
            ) {
                Text("No logs found", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            return
        }

        NetworkLogList(modifier = Modifier, logList = networkLog, onClick = onClick)
    }
}

@Composable
fun NetworkLogList(
    modifier: Modifier = Modifier,
    logList: List<HarEntry>,
    onClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(logList) {
            NetworkLogItemSummary(it) { id ->
                onClick(id)
            }
        }
    }
}

@Composable
fun NetworkLogItemSummary(
    networkLog: HarEntry,
    onClick: (Long) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .debouncedClickable {
                onClick(networkLog.id)
            },
        horizontalArrangement = Arrangement.Start,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.175f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = networkLog.response.status.toString(),
                color = if (networkLog.response.status >= 400) Color.Red else LocalContentColor.current,
                fontWeight = FontWeight.Bold,
            )
            Text(networkLog.request.method)
        }
        Text(text = networkLog.request.url, maxLines = 3, overflow = TextOverflow.Ellipsis)
    }

    val age = networkLog.createdAt / 1000
    Text(
        text = age.ageString(),
        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth(),
    )

    HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
}
//
//@Preview
//@Composable
//fun PreviewNetworkLogScreen() {
//    val logList = listOf(
//        NetworkLog(
//            responseCode = 201,
//            url = "--> GET https://www.omnycontent.com/d/programs/5e27a451-e6e6-4c51-aa03-a7370003783c/68621bca-f318-4ad9-90df-a82600035a72/image.jpg?t=1691192679&size=Large\n",
//            method = "GET",
//        ),
//        NetworkLog(
//            responseCode = 403,
//            url = "https://api.podcastindex.org/api/1.0/podcasts/trending?lang=en-US&cat=technology",
//            method = "POST",
//        ),
//    )
//    NetworkLogList(logList = logList) {}
//}

@Preview
@Composable
fun PreviewEmptyNetworkLogScreen() {
    NetworkLogList(logList = emptyList()) {}
}
