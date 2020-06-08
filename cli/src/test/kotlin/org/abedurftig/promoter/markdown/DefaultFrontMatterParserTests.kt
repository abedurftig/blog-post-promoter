package org.abedurftig.promoter.markdown

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class DefaultFrontMatterParserTests {

    private val defaultFrontMatterParser = DefaultFrontMatterParser()

    @Test
    fun `should return empty map when front matter does not exist`() {

        val frontMatter = """
            This is the actual markdown body.
        """.trimIndent()

        val frontMatterAttributes = defaultFrontMatterParser.readFrontMatterAttributes(frontMatter)

        Assertions.assertThat(frontMatterAttributes.size).isEqualTo(0)
    }

    @Test
    fun `should return empty map when front matter is empty`() {

        val frontMatter = """
            ---
            ---
            This is the actual markdown body.
        """.trimIndent()

        val frontMatterAttributes = defaultFrontMatterParser.readFrontMatterAttributes(frontMatter)

        Assertions.assertThat(frontMatterAttributes.size).isEqualTo(0)
    }

    @Test
    fun `should return correctly populated map`() {

        val title = "This is the title"
        val description = "This is a basic description"

        val frontMatter = """
            ---
            title: $title
            description: $description
            tags:
              - Tag 1
              - Tag 2
            ---
            This is the actual markdown body.
        """.trimIndent()

        val frontMatterAttributes = defaultFrontMatterParser.readFrontMatterAttributes(frontMatter)

        Assertions.assertThat(frontMatterAttributes.size).isEqualTo(3)

        Assertions.assertThat(frontMatterAttributes.containsKey("title")).isEqualTo(true)
        Assertions.assertThat(frontMatterAttributes["title"]).hasSize(1)
        Assertions.assertThat(frontMatterAttributes.getValue("title").first()).isEqualTo(title)

        Assertions.assertThat(frontMatterAttributes.containsKey("description")).isEqualTo(true)
        Assertions.assertThat(frontMatterAttributes["description"]).hasSize(1)
        Assertions.assertThat(frontMatterAttributes.getValue("description").first()).isEqualTo(description)

        Assertions.assertThat(frontMatterAttributes.containsKey("tags")).isEqualTo(true)
        Assertions.assertThat(frontMatterAttributes["tags"]).hasSize(2)
        Assertions.assertThat(frontMatterAttributes.getValue("tags")).containsExactly("Tag 1", "Tag 2")
    }

    @Test
    fun `should return correctly populated map when list has an empty element`() {

        val frontMatter = """
            ---
            tags:
              - 
              - Tag 2
            ---
            This is the actual markdown body.
        """.trimIndent()

        val frontMatterAttributes = defaultFrontMatterParser.readFrontMatterAttributes(frontMatter)

        Assertions.assertThat(frontMatterAttributes.size).isEqualTo(1)

        Assertions.assertThat(frontMatterAttributes.containsKey("tags")).isEqualTo(true)
        Assertions.assertThat(frontMatterAttributes["tags"]).hasSize(1)
        Assertions.assertThat(frontMatterAttributes.getValue("tags")).containsExactly("Tag 2")
    }

    @Test
    fun `should return empty map when only key is list and has no element`() {

        val frontMatter = """
            ---
            tags:
              -
              -
            ---
            This is the actual markdown body.
        """.trimIndent()

        val frontMatterAttributes = defaultFrontMatterParser.readFrontMatterAttributes(frontMatter)

        Assertions.assertThat(frontMatterAttributes.size).isEqualTo(0)
    }

    @Test
    fun `should return skip key when it has no value`() {

        val frontMatter = """
            ---
            tags:
            anotherkey: This key has a value
            ---
            This is the actual markdown body.
        """.trimIndent()

        val frontMatterAttributes = defaultFrontMatterParser.readFrontMatterAttributes(frontMatter)

        Assertions.assertThat(frontMatterAttributes.size).isEqualTo(1)

        Assertions.assertThat(frontMatterAttributes.containsKey("anotherkey")).isEqualTo(true)
        Assertions.assertThat(frontMatterAttributes["anotherkey"]).hasSize(1)
        Assertions.assertThat(frontMatterAttributes.getValue("anotherkey")).containsExactly("This key has a value")
    }
}
