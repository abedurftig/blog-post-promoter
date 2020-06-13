package org.abedurftig.promoter.model

import java.time.LocalDateTime

data class PromoterStatus(
    val postStatusMap: Map<String, StatusEntry>,
    val lastUpdate: LocalDateTime = LocalDateTime.now()
)

data class StatusEntry(
    val checkSum: String,
    val lastUpdate: LocalDateTime = LocalDateTime.now()
)
