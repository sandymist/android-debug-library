package com.sandymist.android.debuglib.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sandymist.android.common.utilities.debouncedClickable

@Composable
fun Header(
    modifier: Modifier = Modifier,
    title: String,
    actionHandler: ActionHandler? = null,
) {
    Row(
        modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)

        actionHandler?.let {
            Icon(
                imageVector = actionHandler.icon,
                contentDescription = actionHandler.contentDescription,
                modifier = Modifier
                    .size(28.dp)
                    .padding(4.dp)
                    .debouncedClickable {
                        actionHandler.handler()
                    },
            )
        }
    }
    HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 4.dp))
}

data class ActionHandler(
    val icon: ImageVector,
    val contentDescription: String,
    val handler: () -> Unit,
) {
    companion object {
        fun deleteHandler(deleteAll: () -> Unit) = ActionHandler(
            icon = Icons.Default.Delete,
            contentDescription = "Delete all",
            handler = deleteAll,
        )
    }
}
