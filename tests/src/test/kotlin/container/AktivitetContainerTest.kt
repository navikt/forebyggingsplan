package container

import api.dto.ValgtAktivitetDTO
import container.helper.TestContainerHelper
import container.helper.withToken
import domene.enVirksomhet
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.http.*
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

    @Test
    fun `skal kunne sette en aktivitet til fullført`() {
        runBlocking {
            val aktivitet = aktivitetApi.velgAktivitet(aktivitetsmalId = "123", orgnr = enVirksomhet.orgnr, block = withToken())
                .body<ValgtAktivitetDTO>()

            aktivitet.fullført shouldBe false

            aktivitetApi.fullførAktivitet(id = aktivitet.id, orgnr = aktivitet.valgtAv.orgnr, block = withToken())
            val aktivitetEtterFullfør = aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr, block = withToken())
                .body<List<ValgtAktivitetDTO>>().single()

            aktivitetEtterFullfør.fullført shouldBe true
        }
    }
}
