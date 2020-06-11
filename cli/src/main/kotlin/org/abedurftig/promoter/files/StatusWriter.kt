package org.abedurftig.promoter.files

import com.google.gson.Gson
import org.abedurftig.promoter.model.PromoterStatus

class StatusWriter(private val filePath: String) {

    private val jsonMapper = Gson()

    fun writeOutStatus(promoterStatus: PromoterStatus) {
        FileWriter.writeToFile(filePath, jsonMapper.toJson(promoterStatus))
    }
}
