package com.sandymist.android.debuglib.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.sandymist.android.debuglib.ui.viewmodel.NetworkLogViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@Suppress("unused")
@Composable
fun NetworkLogScreen(
    modifier: Modifier = Modifier,
    networkLogViewModel: NetworkLogViewModel,
    logCount: Long,
    onClick: (Long) -> Unit,
    searchString: String,
    setSearchString: (String) -> Unit,
) {
    val networkLog by networkLogViewModel.networkLogList.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Header(
            title = "Network log ($logCount)",
            actionHandler = ActionHandler.deleteHandler { networkLogViewModel.clear() },
        )

        NetworkLogList(
            modifier = Modifier,
            logList = networkLog,
            onClick = onClick,
            searchString = searchString,
            setSearchString = setSearchString,
        )
    }
}

@OptIn(FlowPreview::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NetworkLogList(
    modifier: Modifier = Modifier,
    logList: List<HarEntry>,
    onClick: (Long) -> Unit,
    searchString: String,
    setSearchString: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val hasScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }
    val subtleColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    var inputString by remember { mutableStateOf(searchString) }

    LaunchedEffect(searchString) {
        snapshotFlow { inputString }
            .debounce(500)
            .collectLatest {
                setSearchString(it)
            }
    }

    TextField(
        value = inputString,
        onValueChange = { inputString = it },
        placeholder = { Text("Search") },
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            if (inputString.isNotEmpty()) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "",
                    modifier = Modifier.debouncedClickable {
                        inputString = ""
                    },
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(subtleColor, RoundedCornerShape(8.dp)),
    )

    if (logList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("No logs found", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        return
    }

    LaunchedEffect(logList) {
        if (!hasScrolled) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (hasScrolled) {
                FloatingActionButton(onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Add")
                }
            }
        }
    ) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState,
        ) {
            items(items = logList, key = { it.id }) {
                NetworkLogItemSummary(it) { id ->
                    onClick(id)
                }
                HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))
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

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val elapsed = networkLog.time / 1000000
        Text(
            "⏱\uFE0F $elapsed ms",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )

        val age = networkLog.createdAt / 1000
        Text(
            text = "${age.ageString()} ago",
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary,
        )
    }
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
    NetworkLogList(logList = emptyList(), searchString = "", setSearchString = {}, onClick = {})
}
