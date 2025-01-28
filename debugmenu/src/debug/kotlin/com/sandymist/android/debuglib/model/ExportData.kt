package com.sandymist.android.debuglib.model

import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val networkLogList: List<NetworkLog>,
    val logcatList: List<Logcat>,
)