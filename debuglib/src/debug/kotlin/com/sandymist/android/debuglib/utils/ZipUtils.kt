package com.sandymist.android.debuglib.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

// Compress a string
fun String.compressString(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    GZIPOutputStream(outputStream).use { gzip ->
        gzip.write(toByteArray(Charsets.UTF_8))
    }
    return outputStream.toByteArray()
}

// Decompress a byte array back to a string
fun ByteArray.decompressString(): String {
    val inputStream = GZIPInputStream(ByteArrayInputStream(this))
    return inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
}
