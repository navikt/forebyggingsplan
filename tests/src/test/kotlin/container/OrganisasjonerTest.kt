package container

import container.helper.TestContainerHelper
import container.helper.withToken
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import request.AktivitetApi
import kotlin.test.Test

internal class OrganisasjonerTest {
    private val api = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    internal fun `skal kunne hente organisasjoner som er tilknyttet en bruker`() {
        runBlocking {
            val response = api.hentVirksomheter(withToken())
            response.status shouldBe HttpStatusCode.OK
            response.bodyAsText().isNotEmpty() shouldBe true
        }
    }
}