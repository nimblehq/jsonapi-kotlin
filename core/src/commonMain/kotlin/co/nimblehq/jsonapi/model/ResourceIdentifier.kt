package co.nimblehq.jsonapi.model

import kotlinx.serialization.Serializable

interface ResourceIdentifiable {
    val id: String
    val type: String
}

@Serializable
data class ResourceIdentifier(override val id: String, override val type: String) : ResourceIdentifiable

val ResourceIdentifiable.resourceIdentifier: ResourceIdentifier
    get() = ResourceIdentifier(id, type)
