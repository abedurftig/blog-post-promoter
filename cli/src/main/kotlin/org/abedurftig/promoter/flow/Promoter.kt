package org.abedurftig.promoter.flow

import io.vavr.control.Try
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
    val untracked: Set<BlogPost>,
    val skipped: Set<BlogPost>
) {

    override fun toString(): String {
        return "New=${toBePublished.size}, Updated=${toBeUpdated.size}, " +
            "Unmodified=${unmodified.size}, Untracked=${untracked.size}, Skipped=${skipped.size}"
    }
}

class PromotionException(val exceptionMessage: String) : Exception(exceptionMessage)

class Promoter(
    private val blogPostReader: BlogPostReader,
    private val blogPostWriter: BlogPostWriter,
    private val statusService: StatusService,
    private val checksumBuilder: ChecksumBuilder,
    private val devToService: DevToService,
    private val markdownComposer: MarkdownComposer,
    private val gitClient: GitClient
) {

    fun execute(settings: Settings) {

        Log.log("Running with the following settings:")
        Log.log(settings.toString())

        validateDevToToken()

        val contentPath = settings.targetDir + settings.contentDir
        val blogPosts = blogPostReader.readBlogPosts(contentPath)
        val status = statusService.readStatus()

        Log.log("Found ${blogPosts.size} blog posts, the last status update was '${status.lastUpdate}'.")

        val context = buildContext(blogPosts, status, settings)
        Log.log("More detail: $context")

        val statusMap = processBlogPosts(context, status)
        val updatedStatus = PromoterStatus(statusMap)

        statusService.writeStatus(updatedStatus)

        val gitRepoPath = settings.targetDir + File.separator + ".git"
        gitClient.commitNewAndChangedFiles(gitRepoPath)
    }

    private fun validateDevToToken() {
        if (!devToService.verifyToken()) {
            throw PromotionException("The token provided for Dev.to seems invalid; please verify your input.")
        }
    }

    private fun validateBlogPost(blogPost: BlogPost): String? {

        if (!hasCanonicalUrl(blogPost)) {
            return "The blog post with title ${blogPost.title}' is missing the 'canonical_url' attribute."
        }
        return null
    }

    private fun hasCanonicalUrl(blogPost: BlogPost): Boolean {
        val canonicalUrl = blogPost.attributes
            .firstOrNull { it.key == "canonical_url" }?.values?.firstOrNull()
        return canonicalUrl != null && canonicalUrl.isNotBlank()
    }

    private fun processBlogPosts(context: Context, status: PromoterStatus): Map<String, StatusEntry> {

        val statusMap = mutableMapOf<String, StatusEntry>()

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
            Log.log("The blog post with title '${unmodified.title}' has not changed.")
        }

        context.untracked.forEach { untracked ->
            Log.log("The blog post with title '${untracked.title}' is not tracked yet.")
        }

        return statusMap
    }

    private fun handleNewPost(toBePublished: BlogPost): Pair<String, StatusEntry> {
        val publishingAttempt = publishBlogPost(toBePublished)
        return if (publishingAttempt.isFailure) {
            Log.log("Publishing of blog post '${toBePublished.title}' to Dev.to failed.")
            handleModification(toBePublished)
        } else {
            val publishedPost = publishingAttempt.get()
            Log.log("Published blog post '${publishedPost.title}' on Dev.to.")
            handleModification(publishedPost)
        }
    }

    private fun handleUpdatedPost(toBeUpdated: BlogPost): Pair<String, StatusEntry> {
        val updateAttempt = updateBlogPost(toBeUpdated)
        return if (updateAttempt.isFailure) {
            Log.log("Updating blog post '${toBeUpdated.title}' on Dev.to failed.")
            handleModification(toBeUpdated)
        } else {
            val updatedPost = updateAttempt.get()
            Log.log("Updated blog post '${updatedPost.title}' on Dev.to.")
            handleModification(updatedPost)
        }
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
        val skipped = mutableSetOf<BlogPost>()

        blogPosts.forEach { blogPost ->

            val currentChecksum = checksumBuilder.calculateCheckSumFromDist(blogPost.filePath)
            val statusEntry = status.postStatusMap[getFileName(blogPost)]

            validateBlogPost(blogPost)?.let {
                skipped.add(blogPost)
                Log.warn(it)
                return@forEach
            }

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
        return Context(toBePublished, toBeUpdated, unmodified, untracked, skipped)
    }

    private fun publishBlogPost(blogPost: BlogPost): Try<BlogPost> {
        return Try.of {
            val response = devToService.createArticle(blogPost.title, markdownComposer.composeMarkdown(blogPost))
            val newFrontMatter = mutableSetOf(
                FrontMatterAttribute("devToId", setOf(response.id.toString())),
                FrontMatterAttribute("devToUrl", setOf(response.url)),
                *blogPost.attributes.toTypedArray()
            )
            blogPost.copy(attributes = newFrontMatter)
        }
    }

    private fun updateBlogPost(blogPost: BlogPost): Try<BlogPost> {
        return Try.of {
            devToService.updateArticle(getDevToId(blogPost), blogPost.title, markdownComposer.composeMarkdown(blogPost))
            blogPost
        }
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
