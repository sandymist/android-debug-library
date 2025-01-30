package com.sandymist.android.debuglib.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PrefItemSerializer::class)
sealed interface PrefItem {
    @Serializable
    data class Header(val title: String): PrefItem
    @Serializable
    data class Data(val key: String, val value: String): PrefItem
}

object PrefItemSerializer : KSerializer<PrefItem> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("PrefItem") // Custom descriptor for the sealed interface

    override fun serialize(encoder: Encoder, value: PrefItem) {
        when (value) {
            is PrefItem.Header -> encoder.encodeSerializableValue(PrefItem.Header.serializer(), value)
            is PrefItem.Data -> encoder.encodeSerializableValue(PrefItem.Data.serializer(), value)
        }
    }

    override fun deserialize(decoder: Decoder): PrefItem {
        val input = decoder.beginStructure(descriptor)
        return when (val index = input.decodeElementIndex(descriptor)) {
            0 -> input.decodeSerializableElement(descriptor, index, PrefItem.Header.serializer())
            1 -> input.decodeSerializableElement(descriptor, index, PrefItem.Data.serializer())
            else -> throw SerializationException("Unexpected index $index")
        }.also { input.endStructure(descriptor) }
    }
}
