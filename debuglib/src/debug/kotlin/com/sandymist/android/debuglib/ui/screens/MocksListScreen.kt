package com.sandymist.android.debuglib.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sandymist.android.debuglib.mock.MockItem
import com.sandymist.android.debuglib.mock.MocksList
import com.sandymist.android.debuglib.ui.component.Header

@Composable
fun MocksListScreen(
    modifier: Modifier = Modifier,
    getMocks: () -> MocksList,
) {
    var mocks: MocksList by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(Unit) {
        mocks = getMocks()
    }

    if (mocks.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "No mocks found")
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Header(title = "Mocks")
        }

        items(mocks.toList()) { mockItem ->
            MockItem( mockItem = mockItem)
        }
    }
}

@Composable
fun MockItem(mockItem: MockItem) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "Path: ${mockItem.path}")
        Text(text = "Method: ${mockItem.method}")
    }
}
