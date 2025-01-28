package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sandymist.android.debuglib.DebugLib

@Composable
fun SummaryScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext

    Column(modifier = modifier
        .padding(horizontal = 12.dp),
    ) {
        Header(title = "Summary")

        val roomDBSize = DebugLib.getRoomDatabaseSize(context)
        NameValueItem(
            name = "Room DB size",
            value = "$roomDBSize bytes"
        )
    }
}
