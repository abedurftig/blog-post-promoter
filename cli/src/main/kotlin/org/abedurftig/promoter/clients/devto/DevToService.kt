package org.abedurftig.promoter.clients.devto

class DevToService(private val devToApi: DevToApi) {

    fun createArticle(title: String, markdown: String): CreateArticleResponse {
        val createArticleRequest = CreateArticleRequest(Article(title, markdown))
        return devToApi.createArticle(createArticleRequest)
    }
}
