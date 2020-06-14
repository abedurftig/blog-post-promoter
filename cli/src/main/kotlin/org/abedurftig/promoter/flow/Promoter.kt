package org.abedurftig.promoter.flow

import org.abedurftig.promoter.clients.devto.DevToService
import org.abedurftig.promoter.files.BlogPostReader
import org.abedurftig.promoter.files.BlogPostWriter
import org.abedurftig.promoter.files.ChecksumBuilder
import org.abedurftig.promoter.files.StatusService
import org.abedurftig.promoter.markdown.MarkdownComposer
import org.abedurftig.promoter.model.BlogPost
import org.abedurftig.promoter.model.FrontMatterAttribute
import org.abedurftig.promoter.model.PromoterStatus
import org.abedurftig.promoter.model.StatusEntry
import java.io.File

class Context(
    val toBePublished: Set<BlogPost>,
    val toBeUpdated: Set<BlogPost>,
    val unmodified: Set<BlogPost>,
    val untracked: Set<BlogPost>
) {

    override fun toString(): String {
        return "Context - new=${toBePublished.size}, updated=${toBeUpdated.size}, " +
            "unmodified=${unmodified.size}, untracked=${untracked.size}"
    }
}

class Promoter(
    private val blogPostReader: BlogPostReader,
    private val blogPostWriter: BlogPostWriter,
    private val statusService: StatusService,
    private val checksumBuilder: ChecksumBuilder,
    private val devToService: DevToService,
    private val markdownComposer: MarkdownComposer
) {

    fun execute(settings: Settings) {

        Log.log("Running with the following settings:")
        Log.log(settings.toString())

        val contentPath = settings.targetDir + settings.contentDir
        val blogPosts = blogPostReader.readBlogPosts(contentPath)
        val status = statusService.readStatus()
        val statusMap = mutableMapOf<String, StatusEntry>()

        Log.log("Found ${blogPosts.size} blog posts, the last status update was '${status.lastUpdate}'.")

        val context = buildContext(blogPosts, status, settings)
        Log.log(context.toString())

        context.toBePublished.forEach { toBePublished ->
            val statusMapEntryPair = handleNewPost(toBePublished)
            statusMap[statusMapEntryPair.first] = statusMapEntryPair.second
        }

        context.toBeUpdated.forEach { toBeUpdated ->
            val statusMapEntryPair = handleUpdatedPost(toBeUpdated)
            statusMap[statusMapEntryPair.first] = statusMapEntryPair.second
        }

        context.unmodified.forEach { unmodified ->
            val fileName = unmodified.filePath.substringAfterLast(File.separator)
            statusMap[fileName] = status.postStatusMap[getFileName(unmodified)] ?: error("")
        }

        val updatedStatus = PromoterStatus(statusMap)
        statusService.writeStatus(updatedStatus)

//        val git = Git.open(File(settings.targetDir + File.separator + ".git"))
//        Log.log("Found Git repo:" + git.repository.identifier)
//        val gitStatus = git.status().call()
//        gitStatus.untracked.forEach { Log.log(it) }
//        gitStatus.modified.forEach { Log.log(it) }
//        git.add().addFilepattern().call()
//        git.close()
    }

    private fun handleNewPost(toBePublished: BlogPost): Pair<String, StatusEntry> {
        val publishedPost = publishBlogPost(toBePublished)
        Log.log("Published blog post '${publishedPost.title}' on Dev.to.")
        return handleModification(publishedPost)
    }

    private fun handleUpdatedPost(toBeUpdated: BlogPost): Pair<String, StatusEntry> {
        val updatedPost = updateBlogPost(toBeUpdated)
        Log.log("Updated blog post '${updatedPost.title}' on Dev.to.")
        return handleModification(updatedPost)
    }

    private fun handleModification(modifiedBlogPost: BlogPost): Pair<String, StatusEntry> {
        val fileName = modifiedBlogPost.filePath.substringAfterLast(File.separator)
        val markdown = markdownComposer.composeMarkdown(modifiedBlogPost)
        val checksum = checksumBuilder.calculateCheckSum(markdown)
        blogPostWriter.writeOutBlogPost(modifiedBlogPost.filePath, markdown)
        return Pair(fileName, StatusEntry(checksum))
    }

    private fun buildContext(blogPosts: Set<BlogPost>, status: PromoterStatus, settings: Settings): Context {

        val toBePublished = mutableSetOf<BlogPost>()
        val toBeUpdated = mutableSetOf<BlogPost>()
        val unmodified = mutableSetOf<BlogPost>()
        val untracked = mutableSetOf<BlogPost>()

        blogPosts.forEach { blogPost ->

            val currentChecksum = checksumBuilder.calculateCheckSumFromDist(blogPost.filePath)
            val statusEntry = status.postStatusMap[getFileName(blogPost)]

            if (statusEntry == null) {
                if (shouldBePublished(blogPost, settings.publishIf)) {
                    toBePublished.add(blogPost)
                } else {
                    untracked.add(blogPost)
                }
            }

            if (statusEntry != null) {
                if (statusEntry.checkSum != currentChecksum) {
                    toBeUpdated.add(blogPost)
                } else {
                    unmodified.add(blogPost)
                }
            }
        }

        return Context(toBePublished, toBeUpdated, unmodified, untracked)
    }

    private fun publishBlogPost(blogPost: BlogPost): BlogPost {
        val response = devToService.createArticle(blogPost.title, markdownComposer.composeMarkdown(blogPost))
        val newFrontMatter = mutableSetOf(
            FrontMatterAttribute("devToId", setOf(response.id.toString())),
            FrontMatterAttribute("devToUrl", setOf(response.url)),
            *blogPost.attributes.toTypedArray()
        )
        return blogPost.copy(attributes = newFrontMatter)
    }

    private fun updateBlogPost(blogPost: BlogPost): BlogPost {
        devToService.updateArticle(getDevToId(blogPost), blogPost.title, markdownComposer.composeMarkdown(blogPost))
        return blogPost
    }

    private fun getFileName(blogPost: BlogPost): String {
        return blogPost.filePath.substringAfterLast(File.separator)
    }

    private fun getDevToId(blogPost: BlogPost): Int {
        return blogPost.attributes.first { it.key == "devToId" }.values.first().toInt()
    }

    private fun shouldBePublished(blogPost: BlogPost, publishIf: String): Boolean {
        blogPost.attributes.firstOrNull { it.key == publishIf }?.let {
            return it.values.size == 1 && it.values.first().toBoolean()
        }
        return false
    }
}
