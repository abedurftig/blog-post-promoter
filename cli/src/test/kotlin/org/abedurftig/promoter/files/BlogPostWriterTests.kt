package org.abedurftig.promoter.files

import org.abedurftig.promoter.TestData
import org.abedurftig.promoter.markdown.MarkdownComposer
import org.abedurftig.promoter.model.BlogPost
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class BlogPostWriterTests {

    @Test
    fun `should write out blog post to markdown file`() {

        val filePath = "/org/abedurftig/promoter/files/BlogPostWriter/MyFirstPost.md"
        val blogPost = BlogPost(TestData.sampleBody, TestData.sampleFrontMatter, filePath)

        // act
        val blogPostWriter = BlogPostWriter(MarkdownComposer())
        blogPostWriter.writeOutBlogPost(blogPost)

        // assert
        val markdown = String(Files.readAllBytes(Paths.get(this.javaClass.getResource(filePath).toURI())))
        Assertions.assertThat(markdown).isEqualTo(TestData.sampleBlogPost)
    }
}

