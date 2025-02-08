package com.sandymist.android.debuglib.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sandymist.android.debuglib.ui.component.DataItem

@Composable
fun BranchInfoScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        val buildType = context.getAppBuildConfigStringValue("BUILD_TYPE")
        DataItem(label = "Build Type: $buildType")

        DataItem(label = "Branch: " + context.getAppBuildConfigStringValue("GIT_BRANCH"))
        DataItem(label = "Commit: " + context.getAppBuildConfigStringValue("GIT_COMMIT"))
        DataItem(label = "Build time: " + context.getAppBuildConfigLongValue("BUILD_TIME"))
        DataItem(label = "Timezone: " + context.getAppBuildConfigStringValue("BUILD_TIMEZONE"))
        DataItem(label = "Directory: " + context.getAppBuildConfigStringValue("BUILD_DIRECTORY"))
        DataItem(label = "User: " + context.getAppBuildConfigStringValue("BUILD_USER"))
    }
}

fun Context.getAppBuildConfigStringValue(fieldName: String): String? {
    return try {
        val appPackageName = packageName
        val clazz = Class.forName("$appPackageName.BuildConfig")
        val field = clazz.getField(fieldName)
        field.get(null) as? String?
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Context.getAppBuildConfigLongValue(fieldName: String): Long? {
    return try {
        val appPackageName = packageName
        val clazz = Class.forName("$appPackageName.BuildConfig")
        val field = clazz.getField(fieldName)
        field.get(null) as? Long?
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
