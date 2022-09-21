package co.nimblehq.jsonapi.serializer

import co.nimblehq.jsonapi.model.DataType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = DataType::class)
class DataTypeSerializer<T>(private val elementSerializer: KSerializer<T>): KSerializer<DataType<T>> {

    override val descriptor: SerialDescriptor = elementSerializer.descriptor

    override fun deserialize(decoder: Decoder): DataType<T> {
        require(decoder is JsonDecoder)
        val element = decoder.decodeJsonElement()
        return if (element is JsonArray) {
            DataType.Collection(decoder.json.decodeFromJsonElement(ListSerializer(elementSerializer), element))
        } else {
            DataType.Single(decoder.json.decodeFromJsonElement(elementSerializer, element))
        }
    }

    override fun serialize(encoder: Encoder, value: DataType<T>) {
        require(encoder is JsonEncoder)
        val element = when (value) {
            is DataType.Single -> encoder.json.encodeToJsonElement(elementSerializer, value.item)
            is DataType.Collection -> encoder.json.encodeToJsonElement(ListSerializer(elementSerializer), value.items)
        }
        encoder.encodeJsonElement(element)
    }
}
