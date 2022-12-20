package co.nimblehq.jsonapi.model

import co.nimblehq.jsonapi.serializer.ApiJsonSerializer
import co.nimblehq.jsonapi.serializer.LinkSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LinkSerializer::class)
data class Link(
    @Serializable
    val href: String? = null,
    @Serializable(with = ApiJsonSerializer::class) val meta: ApiJson? = null
)

