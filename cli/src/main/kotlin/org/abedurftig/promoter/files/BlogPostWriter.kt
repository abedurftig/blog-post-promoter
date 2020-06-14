package org.abedurftig.promoter.files

import org.abedurftig.promoter.markdown.MarkdownComposer
import org.abedurftig.promoter.model.BlogPost

class BlogPostWriter(private val markdownComposer: MarkdownComposer) {

    fun writeOutBlogPost(blogPost: BlogPost) {
        this.writeOutBlogPost(blogPost.filePath, markdownComposer.composeMarkdown(blogPost))
    }

    fun writeOutBlogPost(filePath: String, markdown: String) {
        FileWriter.writeToFile(
            filePath,
            markdown
        )
    }
}
