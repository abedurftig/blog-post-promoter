package org.abedurftig.promoter.clients

import org.abedurftig.promoter.clients.devto.ClientException
import org.abedurftig.promoter.clients.devto.DevToClient
import org.abedurftig.promoter.clients.devto.DevToService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class DevToServiceIntegrationTests {

    @Test
    fun `should create an article`() {

        val service = DevToService(DevToClient(""))
        val response = service.createArticle("", "")

        Assertions.assertThat(response.id).isEqualTo(150589)
        Assertions.assertThat(response.url).isEqualTo("https://dev.to/bytesized/byte-sized-episode-2-the-creation-of-graph-theory-34g1")
    }

    @Test
    fun `should throw client exception when unauthenticated`() {

        val service = DevToService(DevToClient("wrong_key"))
        Assertions.assertThatThrownBy { service.createArticle("", "") }
            .isInstanceOf(ClientException::class.java)
            .hasMessage("Unauthorized (status: 401)")
    }
}
