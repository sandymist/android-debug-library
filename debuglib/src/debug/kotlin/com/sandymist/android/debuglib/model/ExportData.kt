package com.sandymist.android.debuglib.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ExportData(
    val networkLogList: List<HarEntry>,
    val logcatList: List<Logcat>,
    val preferencesList: List<DataListItem>,
)
