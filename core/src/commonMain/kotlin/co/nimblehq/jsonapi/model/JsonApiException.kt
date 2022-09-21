package co.nimblehq.jsonapi.model

class JsonApiException(val errors: List<JsonApiError>): Throwable()
