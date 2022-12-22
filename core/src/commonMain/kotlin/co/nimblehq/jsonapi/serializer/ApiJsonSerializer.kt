package co.nimblehq.jsonapi.serializer

import co.nimblehq.jsonapi.model.ApiJson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = ApiJson::class)
object ApiJsonSerializer: KSerializer<ApiJson> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ApiJson", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ApiJson {
        require(decoder is JsonDecoder)
        when (val element = decoder.decodeJsonElement()) {
            is JsonPrimitive -> {
                if (element.isString) {
                    return ApiJson.string(element.content)
                }
                if (element.intOrNull != null) {
                    return ApiJson.int(element.int)
                }
                if (element.doubleOrNull != null) {
                    return ApiJson.double(element.double)
                }
                if (element.booleanOrNull != null) {
                    return ApiJson.boolean(element.boolean)
                }
                return ApiJson.nil
            }
            is JsonObject -> return ApiJson.nested(decoder.json.decodeFromJsonElement(
                MapSerializer(
                    String.serializer(), this), element))
            is JsonArray -> return ApiJson.list(decoder.json.decodeFromJsonElement(ListSerializer(this), element))
            is JsonNull -> return ApiJson.nil
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: ApiJson) {
        require(encoder is JsonEncoder)
        when (value) {
            is ApiJson.string -> encoder.encodeString(value.value)
            is ApiJson.int -> encoder.encodeInt(value.value)
            is ApiJson.double -> encoder.encodeDouble(value.value)
            is ApiJson.boolean -> encoder.encodeBoolean(value.value)
            is ApiJson.nested -> encoder.encodeJsonElement(encoder.json.encodeToJsonElement(value.value))
            is ApiJson.list -> encoder.encodeJsonElement(encoder.json.encodeToJsonElement(value.value))
            is ApiJson.nil -> encoder.encodeNull()
        }
    }
}
