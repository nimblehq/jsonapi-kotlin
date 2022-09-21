package co.nimblehq.jsonapi.serializer

import co.nimblehq.jsonapi.model.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = JsonApiObject::class)
object JsonApiObjectSerializer: KSerializer<JsonApiObject> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("JsonApiObject", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): JsonApiObject {
        require(decoder is JsonDecoder)
        val element = decoder.decodeJsonElement()
        val data = element.jsonObject["data"]?.let { decoder.json.decodeFromJsonElement<DataType<Resource>>(it) }
        val errors = element.jsonObject["errors"]?.let { decoder.json.decodeFromJsonElement<List<JsonApiError>>(it) }
        if (data != null && errors != null) {
            throw Exception("Data and errors shouldn't co-exist in the same JSON:API object")
        }
        val meta = element.jsonObject["meta"]?.let { decoder.json.decodeFromJsonElement<ApiJson>(it) }

        val type: JsonApiResponseType = if (data != null) {
            JsonApiResponseType.Data(data)
        } else if (errors != null) {
            JsonApiResponseType.Errors(errors)
        } else if (meta != null) {
            JsonApiResponseType.Meta(meta)
        } else {
            throw Exception("Either one of data, errors, or meta should have value")
        }

        val links = element.jsonObject["links"]?.let { decoder.json.decodeFromJsonElement<Links>(it) }
        val included = element.jsonObject["included"]?.let { decoder.json.decodeFromJsonElement<List<Resource>>(it) }

        return JsonApiObject(type, links, included, meta)
    }

    override fun serialize(encoder: Encoder, value: JsonApiObject) {
        error("Serialization is not supported")
    }
}
