package org.abedurftig.promoter.files

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ChecksumBuilderTests {

    private val checksumBuilder = ChecksumBuilder()

    @Test
    fun `should calculate correct checksum`() {

        val path = this.javaClass.getResource("/org/abedurftig/promoter/files/ChecksumBuilder/Markdown.md").toURI().path
        val checksum = checksumBuilder.calculateCheckSumFromDist(path)

        Assertions.assertThat(checksum).isEqualTo("fb2753a5bc382983310dea39fea7704a")
    }

    @Test
    fun `should handle non-existing path`() {

        val checksum = checksumBuilder.calculateCheckSumFromDist("doesnnotexist/Markdown.md")
        Assertions.assertThat(checksum).isEmpty()
    }
}
