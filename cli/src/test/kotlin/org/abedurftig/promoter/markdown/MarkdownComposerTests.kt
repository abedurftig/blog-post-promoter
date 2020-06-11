package org.abedurftig.promoter.markdown

import org.abedurftig.promoter.TestData
import org.abedurftig.promoter.model.BlogPost
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class MarkdownComposerTests {

    @Test
    fun `should create well formatted markdown content`() {

        val filePath = "/org/abedurftig/promoter/files/BlogPostWriter/MyFirstPost.md"
        val blogPost = BlogPost(TestData.sampleBody, TestData.sampleFrontMatter, filePath)

        // act
        val markdown = MarkdownComposer().composeMarkdown(blogPost)

        // assert
        Assertions.assertThat(markdown).isEqualTo(TestData.sampleBlogPost)
    }
}
