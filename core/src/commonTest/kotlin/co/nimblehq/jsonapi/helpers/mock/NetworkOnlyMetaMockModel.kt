package co.nimblehq.jsonapi.helpers.mock

import kotlinx.serialization.Serializable

@Serializable
data class NetworkOnlyMetaMockModel(
    val message: String
) {

    companion object Response {
        val responseWithMetaOnly = """
        {
          "meta": {
            "message": "Success"
          }
        }
        """.trimIndent()
    }
}
