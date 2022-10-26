# JSON:API Kotlin

Kotlin module for mapping JSON response, in the format of [JSON:API](https://jsonapi.org) to objects.

Made specifically for Kotlin Multiplatform Mobile (KMM).

## Installation

### Git Submodule

1. Add this library as a submodule of your project.

```
git submodule add git@github.com:nimblehq/jsonapi-kotlin.git
```

### Manually

1. Clone the repository

```
git clone git@github.com:nimblehq/jsonapi-kotlin.git
```

2. Copy the cloned folder to the `root` of your project.

## Usage

Open `root` `settings.gradle.kts` and add the library as an include.

```
rootProject.name = "YourApp"
include(":androidApp")
include(":shared")
// .. Add this line
include(":nimble-jsonapi-kotlin:core")
```

### Add Dependency to `shared` Module

Open `build.gradle.kts` of `shared` module, or any KMM module that will use the library.

```
sourceSets {
  val commonMain by getting {
      dependencies {
          // ...
          // Add this line
            implementation(project(":jsonapi-kotlin:core"))
        }
    }
  // ...
}
```

### Use with `kotlinx.serialization.json.Json`

Call `decodeFromJsonApiString` to map string to object.

```
@Serializable
data class ExampleModel(val title: String)
val body = """
{
  "data": {
    "type": "articles",
    "id": "1",
    "attributes": {
      "title": "JSON:API paints my bikeshed!"
    }
  }
}
"""
val json = Json {
     prettyPrint = true
     isLenient = true
     ignoreUnknownKeys = true
 }
val data = JsonApi(json).decodeFromJsonApiString<ExampleModel>(body)
```

For installation example, see this [commit](https://github.com/suho/kmm-ic/pull/89/commits/010631cd00144edecfcc42f3a057c0294fa5571a).

## Features

### JSON:API

This library maps JSON response, in the format of [JSON:API](https://jsonapi.org).

```
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
"""
```

```
@Serializable
data class NetworkMockModel(
    val title: String,
    val body: String,
    val created: String,
    val updated: String,
    @SerialName("item_version")
    val itemVersion: Int
)

val data = JsonApi(Json).decodeFromJsonApiString<NetworkMockModel>(response)
```

### Error

Returns error if object failed to map or the API returns error

```
val response = """
{
  "errors": [
    {
      "source": { "pointer": "" },
      "detail":  "Missing `data` Member at document's top level."
    }
  ]
}
"""
```

```
try {
    val data = JsonApi(Json).decodeFromJsonApiString<NetworkMockModel>(response)
} catch (e: JsonApiException) {
    return e.errors.first().detail // Missing `data` Member at document's top level.
}
```

### Array

```
val response = """
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
"""
```

```
@Serializable
data class NetworkMockModel(
    val title: String,
    val body: String,
    val created: String,
    val updated: String,
    @SerialName("item_version")
    val itemVersion: Int
)

val data = JsonApi(Json).decodeFromJsonApiString<List<NetworkMockModel>>(response)
```

### Relationship & Included

`included` object will be mapped by `relationships` `type`.

```
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
"""
```

```
@Serializable
data class NetworkIncludedMockModel(
    val title: String,
    val body: String,
    val created: String,
    val updated: String,
    val author: People
)

val data = JsonApi(Json).decodeFromJsonApiString<NetworkIncludedMockModel>(response)
```

### Array Included

```
val response = """
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
"""
```

```
@Serializable
data class NetworkIncludedMockModel(
    val title: String,
    val body: String,
    val created: String,
    val updated: String,
    val author: People
)

val data = JsonApi(Json).decodeFromJsonApiString<List<NetworkIncludedMockModel>>(response)
```

## Metadata

Use `decodeWithMetaFromJsonApiString` instead of `decodeFromJsonApiString` to get `meta` along with object.

```
val response = """
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
"""
```

```
@Serializable
data class NetworkMockModel(
    val title: String,
    val body: String,
    val created: String,
    val updated: String,
    @SerialName("item_version")
    val itemVersion: Int
)
@Serializable
data class NetworkMetaMockModel(
    val page: Int,
    val totalPages: Int
)

val data = JsonApi(Json)
  .decodeWithMetaFromJsonApiString<List<NetworkMockModel>, NetworkMetaMockModel>(
    response
  )
```

`decodeWithMetaFromJsonApiString` works with `single`, `array`, and `included`, same as `decodeFromJsonApiString`.

## License

This project is Copyright (c) 2014 and onwards Nimble. It is free software and may be redistributed under the terms specified in the [LICENSE] file.

[LICENSE]: /LICENSE

## About
<a href="https://nimblehq.co/">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://assets.nimblehq.co/logo/dark/logo-dark-text-160.png">
    <img alt="Nimble logo" src="https://assets.nimblehq.co/logo/light/logo-light-text-160.png">
  </picture>
</a>

This project is maintained and funded by Nimble.

We ❤️ open source and do our part in sharing our work with the community!
See [our other projects][community] or [hire our team][hire] to help build your product.

Want to join? [Check out our Jobs][jobs]!

[community]: https://github.com/nimblehq
[hire]: https://nimblehq.co/
[jobs]: https://jobs.nimblehq.co/
