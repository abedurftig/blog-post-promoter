package org.abedurftig.promoter.files

import org.abedurftig.promoter.markdown.BodyParser
import org.abedurftig.promoter.markdown.DefaultBodyParser
import org.abedurftig.promoter.markdown.DefaultFrontMatterParser
import org.abedurftig.promoter.markdown.FrontMatterParser
import org.abedurftig.promoter.model.BlogPost
import org.abedurftig.promoter.model.FrontMatterAttribute
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class BlogPostReader(
    private val frontMatterParser: FrontMatterParser = DefaultFrontMatterParser(),
    private val bodyParser: BodyParser = DefaultBodyParser()
) {

    fun readBlogPosts(folder: String): Set<BlogPost> {
        val folderFile = Paths.get(folder).toFile()
        runBasicValidation(folderFile)
        val blogPostFiles = readMarkdownFiles(folderFile)
        return blogPostFiles.map {
            blogPostFile -> makeBlogPost(blogPostFile)
        }.toSet()
    }

    private fun runBasicValidation(folder: File) {
        if (!folder.exists()) {
            throw IllegalArgumentException("No folder found with path $folder.")
        }
        if (folder.isFile) {
            throw IllegalArgumentException("Input path must be a directory.")
        }
    }

    private fun readMarkdownFiles(folder: File) =
        folder.listFiles { _, name -> name.toLowerCase().endsWith(".md") }?.toSet() ?: emptySet()

    private fun makeBlogPost(blogPostFile: File): BlogPost {

        val markdown = String(Files.readAllBytes(blogPostFile.toPath()))
        val frontMatter = frontMatterParser.readFrontMatterAttributes(markdown)
        val frontMatterAttributes = frontMatter.map { entry -> FrontMatterAttribute(entry.key, entry.value) }.toSet()
        val body = bodyParser.extractBody(markdown)
        return BlogPost(body, frontMatterAttributes, blogPostFile.path)
    }
}
