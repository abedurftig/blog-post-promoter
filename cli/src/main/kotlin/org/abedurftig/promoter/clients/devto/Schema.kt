package org.abedurftig.promoter.clients.devto

data class CreateArticleRequest(val article: Article)

data class CreateArticleResponse(val id: Int, val url: String)

data class Article(
    val title: String,
    val bodyMarkdown: String
)
