package co.nimblehq.jsonapi.model

import co.nimblehq.jsonapi.serializer.ApiJsonSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ApiJsonSerializer::class)
sealed class ApiJson {

    @Serializable(with = ApiJsonSerializer::class)
    data class string(val value: String) : ApiJson()

    @Serializable(with = ApiJsonSerializer::class)
    data class int(val value: Int) : ApiJson()

    @Serializable(with = ApiJsonSerializer::class)
    data class double(val value: Double) : ApiJson()

    @Serializable(with = ApiJsonSerializer::class)
    data class boolean(val value: Boolean) : ApiJson()

    @Serializable(with = ApiJsonSerializer::class)
    data class nested(val value: Map<String, ApiJson>) : ApiJson()

    @Serializable(with = ApiJsonSerializer::class)
    data class list(val value: List<ApiJson>) : ApiJson()

    @Serializable(with = ApiJsonSerializer::class)
    object nil : ApiJson()
}

fun jsonApiNested(apiJson: ApiJson) : Map<String, ApiJson>? {
    return when (apiJson) {
        is ApiJson.nested -> apiJson.value
        else -> null
    }
}
