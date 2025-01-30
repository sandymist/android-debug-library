package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sandymist.android.debuglib.model.PrefItem
import com.sandymist.android.debuglib.ui.viewmodel.PreferencesViewModel

@Suppress("unused")
@Composable
fun PreferencesScreen(
    modifier: Modifier = Modifier,
    preferencesViewModel: PreferencesViewModel,
) {
    val items by preferencesViewModel.preferencesList.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Header(title = "Preferences")

        if (items.isEmpty()) {
            Box(
                modifier = Modifier.weight(0.1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "No preferences found",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            PreferencesList(items)
        }
    }
}

@Composable
fun PreferencesList(preferences: List<PrefItem>) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        items(preferences) { item ->
            when (item) {
                is PrefItem.Header -> {
                    Text(item.title, style = MaterialTheme.typography.headlineSmall)
                    HorizontalDivider(color = Color.DarkGray)
                }
                is PrefItem.Data -> {
                    DataItem(label = "${item.key}: ${item.value}")
//                    HorizontalDivider(color = Color.LightGray)
                }
            }
        }
    }
}
