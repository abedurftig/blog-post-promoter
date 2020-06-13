package org.abedurftig.promoter.flow

import org.abedurftig.promoter.files.BlogPostReader
import org.abedurftig.promoter.files.ChecksumBuilder
import org.abedurftig.promoter.files.StatusService
import org.abedurftig.promoter.model.BlogPost
import org.abedurftig.promoter.model.PromoterStatus
import org.abedurftig.promoter.model.StatusEntry
import java.io.File

class Promoter(
    private val blogPostReader: BlogPostReader,
    private val statusService: StatusService,
    private val checksumBuilder: ChecksumBuilder
) {

    fun execute(settings: Settings) {

        Log.log(settings.toString())

        val contentPath = settings.targetDir + settings.contentDir
        val blogPosts = blogPostReader.readBlogPosts(contentPath)

        Log.log("Found ${blogPosts.size} blog posts.")

        val status = statusService.readStatus()
        val statusMap = mutableMapOf<String, StatusEntry>()

        blogPosts.forEach { blogPost ->
            val fileName = blogPost.filePath.substringAfterLast(File.separator)
            val postStatus = status.postStatusMap[fileName]
            val newChecksum = checksumBuilder.calculateCheckSumFromDist(blogPost.filePath)
            when {
                postStatus == null -> {
                    val shouldBePublished = shouldBePublished(blogPost, settings.publishIf)
                    Log.log("Found new post in file '$fileName' and should be published: '$shouldBePublished'.")
                    if (shouldBePublished) {
                        statusMap[fileName] = StatusEntry(newChecksum)
                    }
                }
                newChecksum == postStatus.checkSum -> {
                    Log.log("Post in file '$fileName' has not changed.")
                    statusMap[fileName] = StatusEntry(postStatus.checkSum, postStatus.lastUpdate)
                }
                else -> {
                    val shouldBePublished = shouldBePublished(blogPost, settings.publishIf)
                    Log.log("Post in file '$fileName' has been updated and should be published: '$shouldBePublished'.")
                    if (shouldBePublished) {
                        statusMap[fileName] = StatusEntry(newChecksum)
                    }
                }
            }
        }
        val updatedStatus = PromoterStatus(statusMap)
        statusService.writeStatus(updatedStatus)
    }

    private fun shouldBePublished(blogPost: BlogPost, publishIf: String): Boolean {
        blogPost.attributes.firstOrNull { it.key == publishIf }?.let {
            return it.values.size == 1 && it.values.first().toBoolean()
        }
        return false
    }
}
