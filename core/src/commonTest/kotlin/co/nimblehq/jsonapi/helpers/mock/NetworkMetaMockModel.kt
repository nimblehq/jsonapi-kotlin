package co.nimblehq.jsonapi.helpers.mock

import kotlinx.serialization.Serializable

@Serializable
data class NetworkMetaMockModel(
    val page: Int,
    val totalPages: Int
) {

    companion object Response {
        val responseWithMeta = """
        {
          "data": [{
            "type": "articles",
            "id": "1",
            "attributes": {
              "title": "JSON:API paints my bikeshed!",
              "body": "The shortest article. Ever.",
              "created": "2015-05-22T14:56:29.000Z",
              "updated": "2015-05-22T14:56:28.000Z",
              "item_version": 2
            }
          }],
          "meta": {
            "page": 2,
            "totalPages": 13
          }
        }
        """.trimIndent()
    }
}
