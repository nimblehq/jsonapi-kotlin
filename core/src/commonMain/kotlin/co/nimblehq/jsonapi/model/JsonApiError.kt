package co.nimblehq.jsonapi.model

import kotlinx.serialization.Serializable

@Serializable
data class JsonApiError(
    @Serializable val id: String? = null,
    @Serializable val title: String? = null,
    @Serializable val detail: String? = null,
    @Serializable val source: Source? = null,
    @Serializable val status: String? = null,
    @Serializable val code: String? = null,
) {

    @Serializable
    data class Source(
        @Serializable val pointer: String? = null,
        @Serializable val parameter: String? = null
    )
}
