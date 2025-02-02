package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TextDemo() {
    val styles = mapOf(
        "displayLarge" to MaterialTheme.typography.displayLarge,
        "displayMedium" to MaterialTheme.typography.displayMedium,
        "displaySmall" to MaterialTheme.typography.displaySmall,
        "headlineLarge" to MaterialTheme.typography.headlineLarge,
        "headlineMedium" to MaterialTheme.typography.headlineMedium,
        "headlineSmall" to MaterialTheme.typography.headlineSmall,
        "titleLarge" to MaterialTheme.typography.titleLarge,
        "titleMedium" to MaterialTheme.typography.titleMedium,
        "titleSmall" to MaterialTheme.typography.titleSmall,
        "bodyLarge" to MaterialTheme.typography.bodyLarge,
        "bodyMedium" to MaterialTheme.typography.bodyMedium,
        "bodySmall" to MaterialTheme.typography.bodySmall,
        "labelLarge" to MaterialTheme.typography.labelLarge,
        "labelMedium" to MaterialTheme.typography.labelMedium,
        "labelSmall" to MaterialTheme.typography.labelSmall,
    ).toList()

    LazyColumn(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
    ) {
        items(styles) {
            Column {
                Text(it.first)
                Text(
                    "Hello, world",
                    textAlign = TextAlign.End,
                    style = it.second,
                    modifier = Modifier.fillMaxWidth(),
                )
                HorizontalDivider()
            }
        }
    }
}

@Preview
@Composable
fun TextDemoPreview() {
    TextDemo()
}
