package com.sandymist.android.debuglib.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.sandymist.android.debuglib.model.HarEntry
import com.sandymist.android.debuglib.model.MockRequest
import com.sandymist.android.debuglib.ui.component.ActionHandler
import com.sandymist.android.debuglib.ui.component.Header
import com.sandymist.android.debuglib.ui.component.ScreenStackRoot

@Composable
fun NetworkLogDetailScreen(
    modifier: Modifier = Modifier,
    getNetworkLog: suspend () -> HarEntry,
    mockRequest: (MockRequest) -> Unit,
    unMockRequest: (String, String) -> Unit,
    isMocked: (String, String) -> Boolean,
) {
    var networkLog: HarEntry? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        networkLog = getNetworkLog()
    }

    if (networkLog == null) {
        Text("Cannot get network log details")
        return
    }

    ScreenStackRoot(
        startScreen = { onNavigateToDetail ->
            NetworkLogDetails(
                modifier,
                networkLog!!,
                onNavigateToDetail,
            )
        },
        detailScreen = {
            MockRequestScreen(
                modifier = modifier,
                networkLog = networkLog!!,
                mockRequest = mockRequest,
                unMockRequest = unMockRequest,
                isMocked = isMocked,
            )
        }
    )
}

@Suppress("unused")
@Composable
fun NetworkLogDetails(
    modifier: Modifier = Modifier,
    networkLog: HarEntry,
    onNavigateToDetail: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

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
                    val content = networkLog.response.toString()
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(content))
                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                }
            )
        )

        NetworkLogItemSummary(networkLog) { }

        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            OutlinedButton(
                onClick = onNavigateToDetail,
            ) {
                Text("Mock this request")
            }
        }
        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))

        Headers("Request Headers", networkLog.request.headers.map { it.toString() })
        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))

        Headers("Response Headers", networkLog.response.headers.map { it.toString() })
        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))

        Text("Body", style = MaterialTheme.typography.headlineMedium)
        Text(networkLog.response.content.text.toString())
    }
}

@Composable
fun Headers(title: String, headerList: List<String>) {
    Text(title, style = MaterialTheme.typography.headlineSmall)
    if (headerList.isEmpty() || (headerList.size == 1 && headerList[0].isEmpty())) {
        Text(text = "No headers found")
    } else {
        headerList.forEach {
            val (name, value) = it.split(":")

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(name) }
                    append(value)
                },
                modifier = Modifier.padding(vertical = 6.dp),
            )
        }
    }
}
