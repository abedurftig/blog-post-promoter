package org.abedurftig.promoter.markdown

class DefaultBodyParser : BodyParser {

    override fun extractBody(markdown: String): String {
        return markdown.substringAfterLast("---\n", markdown)
    }
}
