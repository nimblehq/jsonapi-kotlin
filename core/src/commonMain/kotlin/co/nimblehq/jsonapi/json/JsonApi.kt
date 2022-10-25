package co.nimblehq.jsonapi.json

import co.nimblehq.jsonapi.model.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.serializer

class JsonApi(val json: Json) {

    inline fun <reified T> decodeFromJsonApiString(string: String) : T {
        val jsonApiObject: JsonApiObject = json.decodeFromString(string)
        val includedData = jsonApiObject.included ?: listOf()
        val map = includedMap(includedData)
        return when (val type = jsonApiObject.type) {
            is JsonApiResponseType.Data -> this.decode(type.data, map)
            is JsonApiResponseType.Meta -> this.decode(type.meta)
            is JsonApiResponseType.Errors -> throw JsonApiException(type.errors)
        }
    }

    inline fun <reified Data, reified Meta> decodeWithMetaFromJsonApiString(string: String) : Pair<Data, Meta> {
        val jsonApiObject: JsonApiObject = json.decodeFromString(string)
        val includedData = jsonApiObject.included ?: listOf()
        val map = includedMap(includedData)
        return when (val type = jsonApiObject.type) {
            is JsonApiResponseType.Data ->
                return Pair(
                    this.decode(type.data, map),
                    this.decode(jsonApiObject.meta ?: ApiJson.nil)
                )
            is JsonApiResponseType.Meta ->
                throw JsonApiException("No data field, please use decodeFromJsonApiString instead")
            is JsonApiResponseType.Errors ->
                throw JsonApiException(type.errors)
        }
    }

    inline fun <reified T> decode(dataType: DataType<Resource>, includedMap: Map<ResourceIdentifier, Resource>): T {
        return when (dataType) {
            is DataType.Single -> decodeSingle(dataType.item, includedMap)
            is DataType.Collection -> decodeCollection(dataType.items, includedMap)
        }
    }

    inline fun <reified T> decodeSingle(resource: Resource, includedMap: Map<ResourceIdentifier, Resource>): T {
        val map = resolvedAttributes(resource, includedMap)
        val data = json.encodeToJsonElement(map)
        return json.decodeFromJsonElement(json.serializersModule.serializer(), data)
    }

    inline fun <reified T> decodeCollection(resources: List<Resource>, includedMap: Map<ResourceIdentifier, Resource>): T {
        val collection = resources.map { resolvedAttributes(it, includedMap) }
        val data = json.encodeToJsonElement(collection)
        return json.decodeFromJsonElement(json.serializersModule.serializer(), data)
    }

    inline fun <reified T> decode(meta: ApiJson): T {
        val data = json.encodeToJsonElement(meta)
        return json.decodeFromJsonElement(json.serializersModule.serializer(), data)
    }

    fun resolvedAttributes(resource: Resource, includedMap: Map<ResourceIdentifier, Resource>): ApiJson {
        val attributes = (resource.attributes?.let { jsonApiNested(it) } ?: mapOf()).toMutableMap()
        attributes["id"] = ApiJson.string(resource.id)
        attributes["type"] = ApiJson.string(resource.type)

        resource.relationships?.forEach { entry ->
            when (val data = entry.value.data) {
                is DataType.Single -> {
                    val includedResource = getResource(includedMap, data.item)
                    attributes[entry.key] = resolvedAttributes(includedResource, includedMap)
                }
                is DataType.Collection -> {
                    val includedAttributes = data
                        .items
                        .map { getResource(includedMap, it) }
                        .map { resolvedAttributes(it, includedMap) }
                    attributes[entry.key] = ApiJson.list(includedAttributes)

                }
                else -> { /* DO NOTHING */}
            }
        }
        return ApiJson.nested(attributes)
    }

    fun includedMap(includedData: List<Resource>): Map<ResourceIdentifier, Resource> {
        return includedData.fold(mutableMapOf()) { map, resource ->
            val identifier = ResourceIdentifier(resource.id, resource.type)
            map[identifier] = resource
            map
        }
    }

    private fun getResource(includedMap: Map<ResourceIdentifier, Resource>, identifier: ResourceIdentifier): Resource {
        return includedMap[identifier] ?: Resource(identifier.id, identifier.type)
    }
}
