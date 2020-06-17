package org.abedurftig.promoter.clients.git

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class GitClientTests {

    val gitClient = GitClient("")

    @Test
    fun `should parse username from ssh repo`() {
        val userName = gitClient.parseUsername("git@github.com:abedurftig/blog-post-promoter.git")
        Assertions.assertThat(userName).isEqualTo("abedurftig")
    }

    @Test
    fun `should parse username from https repo`() {
        val userName = gitClient.parseUsername("https://github.com/abedurftig/blog-post-promoter")
        Assertions.assertThat(userName).isEqualTo("abedurftig")
    }
}
