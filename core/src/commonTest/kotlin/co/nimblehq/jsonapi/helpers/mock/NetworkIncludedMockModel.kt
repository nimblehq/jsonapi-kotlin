package co.nimblehq.jsonapi.helpers.mock

import kotlinx.serialization.Serializable

@Serializable
data class NetworkIncludedMockModel(
    val title: String,
    val body: String,
    val created: String,
    val updated: String,
    val author: People
) {

    @Serializable
    data class People(
        val name: String,
        val age: Int,
        val gender: String
    )
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
              "updated": "2015-05-22T14:56:28.000Z"
            },
            "relationships": {
              "author": {
                "data": {"id": "42", "type": "people"}
              }
            }
          },
          "included": [
            {
              "type": "people",
              "id": "42",
              "attributes": {
                "name": "John",
                "age": 80,
                "gender": "male"
              }
            }
          ]
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
              "updated": "2015-05-22T14:56:28.000Z"
            },
            "relationships": {
              "author": {
                "data": {"id": "42", "type": "people"}
              }
            }
          }],
          "included": [
            {
              "type": "people",
              "id": "42",
              "attributes": {
                "name": "John",
                "age": 80,
                "gender": "male"
              }
            }
          ]
        }
        """.trimIndent()
    }
}
