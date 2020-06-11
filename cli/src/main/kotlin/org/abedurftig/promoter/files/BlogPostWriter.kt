package org.abedurftig.promoter.files

import org.abedurftig.promoter.markdown.MarkdownComposer
import org.abedurftig.promoter.model.BlogPost

class BlogPostWriter(private val markdownComposer: MarkdownComposer) {

    fun writeOutBlogPost(blogPost: BlogPost) {
        FileWriter.writeToFile(
            blogPost.filePath,
            markdownComposer.composeMarkdown(blogPost)
        )
    }
}
