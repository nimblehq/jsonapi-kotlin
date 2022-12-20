package co.nimblehq.jsonapi.serializer

import co.nimblehq.jsonapi.model.Link
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Link::class)
object LinkSerializer: KSerializer<Link> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ApiJson", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Link {
        require(decoder is JsonDecoder)
        return when (val element = decoder.decodeJsonElement()) {
            is JsonPrimitive -> {
                val href = element.contentOrNull
                Link(href, null)
            }
            else -> decoder.json.decodeFromJsonElement(element)
        }
    }

    override fun serialize(encoder: Encoder, value: Link) {
        require(encoder is JsonEncoder)
        encoder.encodeJsonElement(encoder.json.encodeToJsonElement(value))
    }
}
