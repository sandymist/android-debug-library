package com.sandymist.android.debuglib.model

import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val networkLogList: List<HarEntry>,
    val logcatList: List<Logcat>,
    val preferencesList: List<DataListItem>,
)
