package org.abedurftig.promoter.model

data class BlogPost(
    val body: String,
    val attributes: Set<FrontMatterAttribute>,
    val filePath: String
)
