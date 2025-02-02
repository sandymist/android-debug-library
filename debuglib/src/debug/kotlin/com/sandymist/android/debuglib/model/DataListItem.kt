package com.sandymist.android.debuglib.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PrefItemSerializer::class)
sealed interface DataListItem {
    @Serializable
    data class Header(val title: String): DataListItem
    @Serializable
    data class Data(val key: String, val value: String = ""): DataListItem
}

object PrefItemSerializer : KSerializer<DataListItem> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("PrefItem") // Custom descriptor for the sealed interface

    override fun serialize(encoder: Encoder, value: DataListItem) {
        when (value) {
            is DataListItem.Header -> encoder.encodeSerializableValue(DataListItem.Header.serializer(), value)
            is DataListItem.Data -> encoder.encodeSerializableValue(DataListItem.Data.serializer(), value)
        }
    }

    override fun deserialize(decoder: Decoder): DataListItem {
        val input = decoder.beginStructure(descriptor)
        return when (val index = input.decodeElementIndex(descriptor)) {
            0 -> input.decodeSerializableElement(descriptor, index, DataListItem.Header.serializer())
            1 -> input.decodeSerializableElement(descriptor, index, DataListItem.Data.serializer())
            else -> throw SerializationException("Unexpected index $index")
        }.also { input.endStructure(descriptor) }
    }
}
