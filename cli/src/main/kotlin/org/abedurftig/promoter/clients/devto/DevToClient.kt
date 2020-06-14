package org.abedurftig.promoter.clients.devto

import feign.Feign
import feign.Headers
import feign.Logger
import feign.RequestInterceptor
import feign.RequestLine
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import feign.okhttp.OkHttpClient
import feign.slf4j.Slf4jLogger
import org.abedurftig.promoter.ApplicationProperties
import org.abedurftig.promoter.model.JsonMapperFactory

interface DevToApi {
    @Headers("auth_token: {token}")
    @RequestLine("POST /api/articles")
    fun createArticle(createArticleRequest: CreateArticleRequest): CreateArticleResponse
}

class DevToClient(private val authKey: String) : DevToApi {

    private val apiClient = getClient()

    override fun createArticle(createArticleRequest: CreateArticleRequest): CreateArticleResponse {
        return apiClient.createArticle(createArticleRequest)
    }

    private fun getClient() = Feign.builder()
        .client(OkHttpClient())
        .requestInterceptor(getRequestInterceptor())
        .decoder(GsonDecoder(JsonMapperFactory.getGson()))
        .encoder(GsonEncoder(JsonMapperFactory.getGson()))
        .logger(Slf4jLogger("DevToClient"))
        .logLevel(Logger.Level.FULL)
        .target(DevToApi::class.java, ApplicationProperties.devToUrl)

    private fun getRequestInterceptor() = RequestInterceptor { template ->
        template.header("api_key", authKey)
        template.header("Content-Type", "application/json")
    }
}
