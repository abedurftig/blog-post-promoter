package org.abedurftig.promoter

import java.util.Properties

object ApplicationProperties {

    private val properties = loadProperties()

    val devToUrl: String
        get() = properties.getProperty("clients.dev-to.url")

    private fun loadProperties(): Properties {

        val loader = Thread.currentThread().contextClassLoader
        val applicationPropertiesStream = loader.getResourceAsStream("application.properties")
        val properties = Properties()
        properties.load(applicationPropertiesStream)

        val testPropertiesStream = loader.getResourceAsStream("application-test.properties")
        if (testPropertiesStream != null) {
            val testProperties = Properties()
            testProperties.load(testPropertiesStream)
            testProperties.forEach { properties[it.key] = it.value }
        }
        return properties
    }
}
