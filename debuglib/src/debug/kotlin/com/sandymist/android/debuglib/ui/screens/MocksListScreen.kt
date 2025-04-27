package com.sandymist.android.debuglib.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sandymist.android.debuglib.model.MockItem
import com.sandymist.android.debuglib.ui.component.Header

@Composable
fun MocksListScreen(
    modifier: Modifier = Modifier,
    mockList: List<MockItem>,
) {
    if (mockList.isEmpty()) {
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
            HorizontalDivider()
        }

        items(mockList) { mockItem ->
            MockItem(mockItem = mockItem)
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
        Text(text = "Enabled: ${mockItem.enabled}")
    }
}
