package org.abedurftig.promoter.files

import org.abedurftig.promoter.markdown.DefaultBodyParser
import org.abedurftig.promoter.markdown.DefaultFrontMatterParser
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class BlogPostReaderTests {

    private val blogPostReader = BlogPostReader(
        DefaultFrontMatterParser(),
        DefaultBodyParser()
    )

    @Test
    fun `throws IllegalArgumentException when file at path does not exist`() {

        val pathToFile = "/org/abedurftig/promoter/files/BlogPostReader/DoesNotExist.txt"
        Assertions.assertThatThrownBy { blogPostReader.readBlogPosts(pathToFile) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("No folder found with path /org/abedurftig/promoter/files/BlogPostReader/DoesNotExist.txt.")
    }

    @Test
    fun `throws IllegalArgumentException when pointed to a file`() {

        val pathToFile = this.javaClass.getResource("/org/abedurftig/promoter/files/BlogPostReader/Somefile.txt").toURI().path
        Assertions.assertThatThrownBy { blogPostReader.readBlogPosts(pathToFile) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Input path must be a directory.")
    }

    @Test
    fun `should read an parse markdown files to BlogPost objects`() {

        val pathToFolder = this.javaClass
            .getResource("/org/abedurftig/promoter/files/BlogPostReader/articles")
            .toURI().path

        val blogPosts = blogPostReader.readBlogPosts(pathToFolder)

        Assertions.assertThat(blogPosts).hasSize(1)

        val blogPost = blogPosts.first()
        val expectedPath = this.javaClass
            .getResource("/org/abedurftig/promoter/files/BlogPostReader/articles/FirstBlogPost.md")
            .toURI().path

        Assertions.assertThat(blogPost.filePath).isEqualTo(expectedPath)
        Assertions.assertThat(blogPost.attributes).hasSize(3)
        Assertions.assertThat(blogPost.body).isEqualTo("This is the actual markdown body.\n")
    }
}
