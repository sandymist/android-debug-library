package com.sandymist.android.debuglib.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sandymist.android.debuglib.model.HarEntry
import com.sandymist.android.debuglib.ui.component.ActionHandler
import com.sandymist.android.debuglib.ui.component.DataItem
import com.sandymist.android.debuglib.ui.component.Header

@Suppress("unused")
@Composable
fun NetworkLogDetailScreen(
    modifier: Modifier = Modifier,
    getNetworkLog: suspend () -> HarEntry,
) {
    var networkLog: HarEntry? by remember { mutableStateOf(null) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

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
        Header(
            title = "Network log",
            actionHandler = ActionHandler(
                icon = Icons.Default.ContentCopy,
                contentDescription = "Copy to clipboard",
                handler = {
                    val content = networkLog!!.response.toString()
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(content))
                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                }
            )
        )

        NetworkLogItemSummary(networkLog!!) {
        }

        Headers("Request Headers", networkLog!!.request.headers.map { it.toString() })
        Headers("Response Headers", networkLog!!.response.headers.map { it.toString() })

        Text("Body", style = MaterialTheme.typography.headlineMedium)
        Text(networkLog!!.response.content.toString())
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
