package org.abedurftig.promoter.flow

import org.slf4j.LoggerFactory

object Log {

    private val LOG = LoggerFactory.getLogger("Promoter")

    fun log(message: String) {
        LOG.info(message)
    }

    fun warn(message: String) {
        LOG.warn(message)
    }
}
