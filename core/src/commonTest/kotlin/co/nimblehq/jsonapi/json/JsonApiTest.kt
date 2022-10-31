package co.nimblehq.jsonapi

import co.nimblehq.jsonapi.helpers.mock.NetworkIncludedMockModel
import co.nimblehq.jsonapi.helpers.mock.NetworkMetaMockModel
import co.nimblehq.jsonapi.helpers.mock.NetworkMockModel
import co.nimblehq.jsonapi.helpers.mock.NetworkOnlyMetaMockModel
import co.nimblehq.jsonapi.json.JsonApi
import co.nimblehq.jsonapi.model.JsonApiException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class JsonApiTest {

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
    var jsonApi = JsonApi(json)

    @Test
    fun `when calling decodeFromJsonApiString with single object it returns correct object`() {
        val decoded = jsonApi.decodeFromJsonApiString<NetworkMockModel>(
            NetworkMockModel.response
        )
        assertEquals(decoded.title, "JSON:API paints my bikeshed!")
        assertEquals(decoded.body, "The shortest article. Ever.")
        assertEquals(decoded.created, "2015-05-22T14:56:29.000Z")
        assertEquals(decoded.itemVersion, 2)
    }

    @Test
    fun `when calling decodeFromJsonApiString with array object it returns correct object`() {
        val decoded = jsonApi.decodeFromJsonApiString<List<NetworkMockModel>>(
            NetworkMockModel.arrayResponse
        )
        assertEquals(decoded.size, 1)
        assertEquals(decoded.first().title, "JSON:API paints my bikeshed!")
        assertEquals(decoded.first().body, "The shortest article. Ever.")
        assertEquals(decoded.first().created, "2015-05-22T14:56:29.000Z")
        assertEquals(decoded.first().itemVersion, 2)
    }

    @Test
    fun `when calling decodeFromJsonApiString with single object with included it returns correct object`() {
        val decoded = jsonApi.decodeFromJsonApiString<NetworkIncludedMockModel>(
            NetworkIncludedMockModel.response
        )
        assertEquals(decoded.title, "JSON:API paints my bikeshed!")
        assertEquals(decoded.body, "The shortest article. Ever.")
        assertEquals(decoded.created, "2015-05-22T14:56:29.000Z")
        assertEquals(decoded.author.name, "John")
    }

    @Test
    fun `when calling decodeFromJsonApiString with array object with included it returns correct object`() {
        val decoded = jsonApi.decodeFromJsonApiString<List<NetworkIncludedMockModel>>(
            NetworkIncludedMockModel.arrayResponse
        )
        assertEquals(decoded.size, 1)
        assertEquals(decoded.first().title, "JSON:API paints my bikeshed!")
        assertEquals(decoded.first().body, "The shortest article. Ever.")
        assertEquals(decoded.first().created, "2015-05-22T14:56:29.000Z")
        assertEquals(decoded.first().author.name, "John")
    }

    @Test
    fun `when calling decodeFromJsonApiString with single object with only meta it returns correct object`() {
        val decoded = jsonApi.decodeFromJsonApiString<NetworkOnlyMetaMockModel>(
            NetworkOnlyMetaMockModel.responseWithMetaOnly
        )
        assertEquals(decoded.message, "Success")
    }

    @Test
    fun `when calling decodeWithMetaFromJsonApiString with array object it returns correct object`() {
        val decoded = jsonApi.decodeWithMetaFromJsonApiString<List<NetworkMockModel>, NetworkMetaMockModel>(
            NetworkMetaMockModel.responseWithMeta
        )
        assertEquals(decoded.first.size, 1)
        assertEquals(decoded.first.first().title, "JSON:API paints my bikeshed!")
        assertEquals(decoded.first.first().body, "The shortest article. Ever.")
        assertEquals(decoded.first.first().created, "2015-05-22T14:56:29.000Z")
        assertEquals(decoded.first.first().itemVersion, 2)
        assertEquals(decoded.second.page, 2)
        assertEquals(decoded.second.totalPages, 13)
    }

    @Test
    fun `when calling decodeFromJsonApiString with error response it returns correct error`() {
        try {
            val decoded = jsonApi.decodeFromJsonApiString<NetworkMockModel>(
                NetworkMockModel.errorResponse
            )
            fail("Should not parse error")
        } catch (e: JsonApiException) {
            assertEquals(e.errors.first().detail, "Missing `data` Member at document's top level.")
        }
    }
}
