package org.abedurftig.promoter.files

import org.abedurftig.promoter.flow.Log
import org.abedurftig.promoter.model.JsonMapperFactory
import org.abedurftig.promoter.model.PromoterStatus
import java.nio.file.Files
import java.nio.file.Paths

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
            Log.log("Did not find a '$fileName' file")
            return PromoterStatus(emptyMap())
        }
        val content = String(Files.readAllBytes(statusFile.toPath()))
        return JsonMapperFactory.getGson().fromJson(content, PromoterStatus::class.java)
    }
}
