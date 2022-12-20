package co.nimblehq.jsonapi.helpers.mock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMockModel(
    val title: String,
    val body: String,
    val created: String,
    val updated: String,
    @SerialName("item_version")
    val itemVersion: Int
) {

    companion object Response {
        val response = """
        {
          "data": {
            "type": "articles",
            "id": "1",
            "attributes": {
              "title": "JSON:API paints my bikeshed!",
              "body": "The shortest article. Ever.",
              "created": "2015-05-22T14:56:29.000Z",
              "updated": "2015-05-22T14:56:28.000Z",
              "item_version": 2
            }
          }
        }
        """.trimIndent()

        val arrayResponse = """
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
          }]
        }
        """.trimIndent()

        val errorResponse = """
        {
          "errors": [
            {
              "source": { "pointer": "" },
              "detail":  "Missing `data` Member at document's top level."
            }
          ]
        }
        """.trimIndent()
    }
}
