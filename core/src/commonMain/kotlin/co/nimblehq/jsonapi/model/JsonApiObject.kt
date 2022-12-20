package co.nimblehq.jsonapi.model

import co.nimblehq.jsonapi.serializer.JsonApiObjectSerializer
import kotlinx.serialization.Serializable


sealed class JsonApiResponseType {
    data class Data(val data: DataType<Resource>) : JsonApiResponseType()
    data class Errors(val errors: List<JsonApiError>) : JsonApiResponseType()
    data class Meta(val meta: ApiJson) : JsonApiResponseType()
}

@Serializable(with = JsonApiObjectSerializer::class)
data class JsonApiObject(
    val type: JsonApiResponseType,
    val links: Links? = null,
    val included: List<Resource>? = null,
    val meta: ApiJson? = null
)

