package org.abedurftig.promoter.markdown

import org.abedurftig.promoter.model.BlogPost
import org.abedurftig.promoter.model.FrontMatterAttribute

class MarkdownComposer {

    fun composeMarkdown(blogPost: BlogPost): String {

        val stringBuilder = StringBuilder()
        stringBuilder.append("---\n")
        blogPost.attributes.forEach { attribute ->
            if (attribute.values.size > 1) appendList(stringBuilder, attribute)
            else appendOne(stringBuilder, attribute)
        }
        stringBuilder.append("---\n")
        stringBuilder.append(blogPost.body)
        return stringBuilder.toString()
    }

    private fun appendList(stringBuilder: StringBuilder, attribute: FrontMatterAttribute) {
        stringBuilder.append("${attribute.key}:\n")
        attribute.values.forEach { value ->
            stringBuilder.append("  - ${value}\n")
        }
    }

    private fun appendOne(stringBuilder: StringBuilder, attribute: FrontMatterAttribute) {
        stringBuilder.append("${attribute.key}: ${attribute.values.first()}\n")
    }
}
