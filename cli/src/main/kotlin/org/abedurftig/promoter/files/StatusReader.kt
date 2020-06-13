package org.abedurftig.promoter.files

import com.google.gson.Gson
import org.abedurftig.promoter.flow.Log
import org.abedurftig.promoter.model.JsonMapperFactory
import org.abedurftig.promoter.model.PromoterStatus
import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

class StatusReader(private val contentPath: String, private val fileName: String) {

    fun readPromoterStatus(): PromoterStatus {
        val contentFolder = Paths.get(contentPath).toFile()
        if (!contentFolder.exists()) {
            throw IllegalStateException("The specified content path does not exist.")
        }
        if (contentFolder.isFile) {
            throw IllegalStateException("The specified content folder is a file.")
        }
        val statusFile = Paths.get(contentFolder.absolutePath + "/" + fileName).toFile()
        if (!statusFile.exists()) {
            Log.log("Did not find a '${fileName}' file")
            return PromoterStatus(emptyMap())
        }
        val content = String(Files.readAllBytes(statusFile.toPath()))
        return JsonMapperFactory.getMapper().fromJson(content, PromoterStatus::class.java)
    }
}
