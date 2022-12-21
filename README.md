# JSON:API Kotlin

Kotlin module for mapping JSON response, in the format of [JSON:API](https://jsonapi.org) to objects.

Made specifically for Kotlin Multiplatform Mobile (KMM).

## Installation

Following [Working with the Gradle registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry)
docs:

- Add GitHub Packages repository at the root project level.

  ```
  repositories {
     maven {
         name = "Github Packages"
         url = uri("https://maven.pkg.github.com/nimblehq/jsonapi-kotlin")
         credentials {
             username = GITHUB_USER
             password = GITHUB_TOKEN
         }
     }
  }
  ```

  - GITHUB_USER: the developer's GitHub user to access GitHub Packages.
  - GITHUB_TOKEN: the developer's GitHub personal access token with at least `packages:read` permission.

- Open `build.gradle.kts` of `shared` module, or any KMM module that will use the library.

  ```
  kotlin {
      sourceSets {
          val commonMain by getting {
              dependencies {
                  implementation("co.nimblehq.jsonapi:core:${LATEST_VERSION}")
              }
          }
          ...
      }
      ...
  }
  ```

## Usage

- Use with `kotlinx.serialization.json.Json`. Call `decodeFromJsonApiString` to map string to object.

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

### Only Meta

When mapping response with only `meta` and no `data`, pass the meta object as the resource type.

```
val response = """
{
  "meta": {
    "message": "Success"
  }
}
""".trimIndent()
```

```
@Serializable
data class NetworkOnlyMetaMockModel(
    val message: String
)

val data = JsonApi(Json).decodeFromJsonApiString<NetworkOnlyMetaMockModel>(response)
```

### Meta

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
