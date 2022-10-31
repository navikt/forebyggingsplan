package container

import container.helper.TestContainerHelper
import container.helper.withToken
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import request.AktivitetApi
import kotlin.test.Test

class AktivitetContainerTest {
    private val aktivitetApi = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    fun `skal få 403 Forbidden dersom man ikke har tilgang i Altinn`() {

        runBlocking {
            aktivitetApi.hentValgteAktiviteterForVirksomhet(
                orgnr = "999999999",
                withToken()
            ).status shouldBe HttpStatusCode.Forbidden
        }
    }
}
