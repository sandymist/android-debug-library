package com.sandymist.android.debuglib.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DataItem(
    modifier: Modifier = Modifier,
    label: String,
) {
    Text(
        label,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    )
}

@Preview
@Composable
fun PreviewDataItem() {
    DataItem(label = "Label")
}
