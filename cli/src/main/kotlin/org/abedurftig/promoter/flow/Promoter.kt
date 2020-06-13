package org.abedurftig.promoter.flow

import org.abedurftig.promoter.files.BlogPostReader
import org.abedurftig.promoter.files.ChecksumBuilder
import org.abedurftig.promoter.files.StatusService
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
            if (postStatus == null) {
                Log.log("Found new post in file '$fileName'.")
            } else {
                if (newChecksum != postStatus.checkSum) {
                    Log.log("Post in file '$fileName' has been updated.")
                } else {
                    Log.log("Post in file '$fileName' has not changed.")
                }
            }
            statusMap[fileName] = StatusEntry(newChecksum)
        }
        val updatedStatus = PromoterStatus(statusMap)
        statusService.writeStatus(updatedStatus)
    }
}
