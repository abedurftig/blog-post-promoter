package org.abedurftig.promoter.files

import org.abedurftig.promoter.flow.Settings
import org.abedurftig.promoter.model.PromoterStatus
import java.io.File

class StatusService private constructor(
    private val statusReader: StatusReader,
    private val statusWriter: StatusWriter
) {

    companion object {
        private const val FILE_NAME = "promoter.status"
        private fun getStatusPath(settings: Settings): String {
            return settings.targetDir + settings.contentDir + File.separator
        }
    }

    constructor(settings: Settings) : this(
        StatusReader(getStatusPath(settings), FILE_NAME),
        StatusWriter(getStatusPath(settings), FILE_NAME)
    )

    fun readStatus(): PromoterStatus {
        return statusReader.readPromoterStatus()
    }

    fun writeStatus(promoterStatus: PromoterStatus) {
        statusWriter.writeOutStatus(promoterStatus)
    }
}
