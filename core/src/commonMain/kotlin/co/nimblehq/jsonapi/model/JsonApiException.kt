package co.nimblehq.jsonapi.model

class JsonApiException(val errors: List<JsonApiError>): Throwable() {
    constructor(message: String) : this(errors = listOf(JsonApiError(detail = message)))
}
