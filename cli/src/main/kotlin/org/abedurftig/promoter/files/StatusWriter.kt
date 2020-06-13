package org.abedurftig.promoter.files

import org.abedurftig.promoter.model.JsonMapperFactory
import org.abedurftig.promoter.model.PromoterStatus

class StatusWriter(private val contentPath: String, private val fileName: String) {

    fun writeOutStatus(promoterStatus: PromoterStatus) {
        FileWriter.writeToFile(contentPath + fileName, JsonMapperFactory.getMapper().toJson(promoterStatus))
    }
}
