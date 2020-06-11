package org.abedurftig.promoter

import org.abedurftig.promoter.model.FrontMatterAttribute

object TestData {

    val sampleBlogPost = """
        ---
        title: This is the title of my first post
        description: This is the desc of my first post
        tags:
          - Development
          - Kotlin
        ---
        This is my blog post!

        I event have some blank lines.
        
    """.trimIndent()

    val sampleBody = """
        This is my blog post!
        
        I event have some blank lines.
        
    """.trimIndent()

    val sampleFrontMatter = setOf(
        FrontMatterAttribute("title", setOf("This is the title of my first post")),
        FrontMatterAttribute("description", setOf("This is the desc of my first post")),
        FrontMatterAttribute("tags", setOf("Development", "Kotlin"))
    )
}
