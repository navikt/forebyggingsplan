package container

import api.endepunkt.AltinnVirksomhetDTO
import container.helper.TestContainerHelper
import container.helper.withToken
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import request.AktivitetApi

internal class OrganisasjonerTest {
    private val api = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    internal fun `skal kunne hente alle organisasjoner som er tilknyttet en bruker`() {
        runBlocking {
            val response = api.hentVirksomheter(withToken())
            response.status shouldBe HttpStatusCode.OK
            response.body<List<AltinnVirksomhetDTO>>() shouldHaveSize 2
        }
    }

    @Test
    internal fun `skal få 401 unauthorized hvis man ikke har et token`() {
        runBlocking {
            val response = api.hentVirksomheter()
            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }
}
