package org.abedurftig.promoter.files

import org.abedurftig.promoter.markdown.MarkdownComposer
import org.abedurftig.promoter.model.BlogPost
import java.nio.file.Files
import java.nio.file.Paths

class BlogPostWriter(private val markdownComposer: MarkdownComposer) {

    fun writeOutBlogPost(blogPost: BlogPost) {
        Files.writeString(
            Paths.get(this.javaClass.getResource(blogPost.filePath).toURI()),
            markdownComposer.composeMarkdown(blogPost)
        )
    }
}
