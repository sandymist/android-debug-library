package com.sandymist.android.debuglib.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
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
    dividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 12.dp),
        )
        HorizontalDivider(color = dividerColor)
    }
}

@Preview
@Composable
fun PreviewDataItem() {
    DataItem(label = "Label")
}
