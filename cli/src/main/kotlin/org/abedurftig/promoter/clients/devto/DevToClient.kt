package org.abedurftig.promoter.clients.devto

import org.abedurftig.promoter.ApplicationProperties
import org.abedurftig.promoter.model.JsonMapperFactory
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status

interface DevToApi {

    fun createArticle(createArticleRequest: CreateArticleRequest): CreateArticleResponse

    fun updateArticle(id: Int, updateArticleRequest: UpdateArticleRequest): UpdateArticleResponse
}

class ClientException(message: String, private val status: Int) : Exception(message) {

    override fun toString(): String {
        return "$message (status: $status)"
    }
}

class DevToClient(private val authKey: String) : DevToApi {

    val client = ApacheClient()

    override fun createArticle(createArticleRequest: CreateArticleRequest): CreateArticleResponse {

        val request = Request(Method.POST, ApplicationProperties.devToUrl + "/api/articles")
            .body(JsonMapperFactory.getGson().toJson(createArticleRequest))
            .header("api_key", authKey)
            .header("Content-Type", "application/json")

        val response = client(request)
        if (response.status == Status.CREATED) {
            return JsonMapperFactory.getGson().fromJson(response.bodyString(), CreateArticleResponse::class.java)
        }
        throw ClientException(response.bodyString(), response.status.code)
    }

    override fun updateArticle(id: Int, updateArticleRequest: UpdateArticleRequest): UpdateArticleResponse {

        val request = Request(Method.PUT, ApplicationProperties.devToUrl + "/api/articles/$id")
            .body(JsonMapperFactory.getGson().toJson(updateArticleRequest))
            .header("api_key", authKey)
            .header("Content-Type", "application/json")

        val response = client(request)
        if (response.status == Status.CREATED) {
            return JsonMapperFactory.getGson().fromJson(response.bodyString(), UpdateArticleResponse::class.java)
        }
        throw ClientException(response.bodyString(), response.status.code)
    }
}
