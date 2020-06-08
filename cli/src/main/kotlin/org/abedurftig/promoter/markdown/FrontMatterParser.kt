package org.abedurftig.promoter.markdown

interface FrontMatterParser {

    fun readFrontMatterAttributes(frontMatter: String): Map<String, Set<String>>
}
