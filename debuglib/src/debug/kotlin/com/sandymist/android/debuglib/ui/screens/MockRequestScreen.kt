package com.sandymist.android.debuglib.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sandymist.android.debuglib.model.HarEntry
import com.sandymist.android.debuglib.model.MockRequest
import com.sandymist.android.debuglib.ui.component.Header
import com.sandymist.android.debuglib.utils.TEST_HAR_ENTRY
import kotlinx.coroutines.launch

@Composable
fun MockRequestScreen(
    modifier: Modifier = Modifier,
    networkLog: HarEntry,
    mockRequest: suspend (MockRequest) -> Unit,
    unMockRequest: suspend (String, String) -> Unit,
    isMocked: suspend (String, String) -> Boolean,
) {
    val context = LocalContext.current
    var code by remember { mutableStateOf(networkLog.response.status.toString()) }
    var body by remember {
        mutableStateOf(networkLog.response.content.text.toString())
    }
    var mocked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(networkLog) {
        mocked = isMocked(networkLog.request.url, networkLog.request.method)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Header(
            title = "Mock request",
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = code,
            onValueChange = {
                code = it
            },
            label = {
                Text(text = "Code")
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        )

        TextField(
            modifier = Modifier.weight(1f),
            value = body,
            onValueChange = {
                body = it
            },
            label = {
                Text(text = "Body")
            },
            maxLines = 60,
        )

        Button(
            onClick = {
                scope.launch {
                    if (mocked) {
                        unMockRequest(networkLog.request.url, networkLog.request.method)
                        Toast.makeText(context, "Un-mock request sent", Toast.LENGTH_SHORT).show()
                    } else {
                        mockRequest(
                            MockRequest(
                                path = networkLog.request.url,
                                method = networkLog.request.method,
                                body = body,
                                code = code.toIntOrNull() ?: 0, // TODO: handle invalid code
                            )
                        )
                    }
                    Toast.makeText(context, "Mock request sent", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(text = if (mocked) "Un mock" else "Mock")
        }
    }
}

@Preview
@Composable
fun PreviewMockRequestScreen() {
    MockRequestScreen(
        networkLog = TEST_HAR_ENTRY,
        mockRequest = {},
        unMockRequest = { _, _ -> },
        isMocked = { _, _ -> true },
    )
}
