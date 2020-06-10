package org.abedurftig.promoter.markdown

interface BodyParser {

    fun extractBody(markdown: String): String
}
