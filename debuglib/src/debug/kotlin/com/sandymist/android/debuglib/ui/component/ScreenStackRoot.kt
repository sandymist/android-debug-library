package com.sandymist.android.debuglib.ui.component

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

@Composable
fun ScreenStackRoot(
    startScreen: @Composable (navigateToDetail: () -> Unit) -> Unit,
    detailScreen: @Composable () -> Unit
) {
    val screenStack = remember { mutableStateListOf<@Composable () -> Unit>() }

    BackHandler(enabled = screenStack.size > 1) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            screenStack.removeLast()
        } else {
            screenStack.removeAt(screenStack.lastIndex)
        }
    }

    if (screenStack.isEmpty()) {
        screenStack.add { startScreen { screenStack.add { detailScreen() } } }
    }

    screenStack.last().invoke()
}
