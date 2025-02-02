package com.sandymist.android.debuglib.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

fun String?.isContentTypeJson(): Boolean {
    return this?.startsWith("application/json") ?: false
}

fun String?.isPrintable(): Boolean {
    val printableTypes = listOf(
        "text/",         // All text types (e.g., text/plain, text/html)
        "application/json",
        "application/xml",
        "application/javascript",
        "application/x-www-form-urlencoded"
    )

    return printableTypes.any { this?.startsWith(it) == true }
}

fun String.prettyPrintJson(): String {
    return try {
        val jsonParser = Json { prettyPrint = true }
        val jsonElement = Json.parseToJsonElement(this)
        jsonParser.encodeToString(JsonObject.serializer(), jsonElement.jsonObject)
    } catch (e: Exception) {
        "Invalid JSON: $this - ${e.message}"
    }
}
