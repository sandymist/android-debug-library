package com.sandymist.android.debuglib.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class HarData(
    @SerialName("log")
    val log: Log?
) {
    @Keep
    @Serializable
    data class Log(
        @SerialName("creator")
        val creator: Creator?,
        @SerialName("entries")
        val entries: List<HarEntry?>?,
        @SerialName("pages")
        val pages: List<Page?>?,
        @SerialName("version")
        val version: String?
    ) {
        @Keep
        @Serializable
        data class Creator(
            @SerialName("name")
            val name: String?,
            @SerialName("version")
            val version: String?
        )

        @Keep
        @Serializable
        data class Page(
            @SerialName("id")
            val id: String?,
            @SerialName("pageTimings")
            val pageTimings: PageTimings?,
            @SerialName("startedDateTime")
            val startedDateTime: String?,
            @SerialName("title")
            val title: String?
        ) {
            @Keep
            @Serializable
            data class PageTimings(
                @SerialName("onContentLoad")
                val onContentLoad: Int?,
                @SerialName("onLoad")
                val onLoad: Int?
            )
        }
    }
}