package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sandymist.android.debuglib.model.NetworkLog

@Suppress("unused")
@Composable
fun NetworkLogDetailScreen(
    modifier: Modifier = Modifier,
    getNetworkLog: suspend () -> NetworkLog,
) {
    var networkLog: NetworkLog? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        networkLog = getNetworkLog()
    }

    if (networkLog == null) {
        Text("Cannot get network log details")
        return
    }

    Column(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
    ) {
        Header(title = "Network log")

        NetworkLogItemSummary(networkLog!!) {
        }

        Headers("Request Headers", networkLog!!.requestHeaders)
        Headers("Response Headers", networkLog!!.responseHeaders)

        Text("Body", style = MaterialTheme.typography.headlineMedium)
        Text(networkLog!!.body)
    }
}

@Composable
fun Headers(title: String, headerList: List<String>) {
    Text(title, style = MaterialTheme.typography.headlineSmall)
    if (headerList.isEmpty() || (headerList.size == 1 && headerList[0].isEmpty())) {
        DataItem(label = "No headers found")
    } else {
        headerList.forEach {
            DataItem(label = it)
        }
    }
}

@Preview
@Composable
fun PreviewNetworkLogGoodDetailScreen() {
    val networkLog = NetworkLog(
        id = "1",
        responseCode = 201,
        url = "--> GET https://www.omnycontent.com/d/programs/5e27a451-e6e6-4c51-aa03-a7370003783c/68621bca-f318-4ad9-90df-a82600035a72/image.jpg?t=1691192679&size=Large\n",
        method = "GET",
    )
    NetworkLogDetailScreen(getNetworkLog = { networkLog })
}

@Preview
@Composable
fun PreviewNetworkLogBadDetailScreen() {
    val networkLog =  NetworkLog(
        id = "2",
        responseCode = 403,
        url = "https://api.podcastindex.org/api/1.0/podcasts/trending?lang=en-US&cat=technology",
        method = "POST",
    )
    NetworkLogDetailScreen(getNetworkLog = { networkLog })
}
