package org.abedurftig.promoter.markdown

interface FrontMatterParser {

    fun readFrontMatterAttributes(markdown: String): Map<String, Set<String>>
}
