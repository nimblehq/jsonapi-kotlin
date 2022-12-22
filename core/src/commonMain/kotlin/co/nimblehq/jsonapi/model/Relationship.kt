package co.nimblehq.jsonapi.model

import co.nimblehq.jsonapi.serializer.ApiJsonSerializer
import co.nimblehq.jsonapi.serializer.DataTypeSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Relationship(
    @Serializable val links: Links? = null,
    @Serializable(with = DataTypeSerializer::class) val data: DataType<ResourceIdentifier>? = null,
    @Serializable(with = ApiJsonSerializer::class) val meta: ApiJson? = null
)
