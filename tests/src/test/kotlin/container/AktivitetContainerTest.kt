package container

import api.dto.ValgtAktivitetDTO
import container.helper.TestContainerHelper
import container.helper.withToken
import enVirksomhet
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import request.AktivitetApi
import kotlin.test.Test

class AktivitetContainerTest {
    private val aktivitetApi = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    fun `skal kunne hente valgte aktiviteter`() {

        runBlocking {
            aktivitetApi.velgAktivitet(aktivitetsmalId = "123", orgnr = enVirksomhet.orgnr, block = withToken())
                .body<ValgtAktivitetDTO>()
            aktivitetApi.velgAktivitet(aktivitetsmalId = "1234", orgnr = enVirksomhet.orgnr, block = withToken())
                .body<ValgtAktivitetDTO>()
            val resultat = aktivitetApi.hentValgteAktiviteterForVirksomhet(
                orgnr = enVirksomhet.orgnr,
                withToken()
            )
            resultat.status shouldBe HttpStatusCode.OK
            resultat.body<List<ValgtAktivitetDTO>>() shouldHaveAtLeastSize 2
        }
    }

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
    fun `skal kunne velge en aktivitet`() {
        runBlocking {
            val aktivitet = aktivitetApi.velgAktivitet(aktivitetsmalId = "123", orgnr = enVirksomhet.orgnr, block = withToken())
                .body<ValgtAktivitetDTO>()

            val hentetAktivitet =
                aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr, block = withToken())
                    .body<List<ValgtAktivitetDTO>>().filter { it.id == aktivitet.id }.single()

            hentetAktivitet.fullført shouldBe false
        }
    }

    @Test
    fun `skal ikke kunne velge en aktivitet i feil organisasjon`() {
        runBlocking {
            aktivitetApi.velgAktivitet(aktivitetsmalId = "123", orgnr = "999999999", block = withToken())
                .status shouldBe HttpStatusCode.Forbidden
        }
    }

    @Test
    fun `skal kunne sette en aktivitet til fullført`() {
        runBlocking {
            val aktivitet = aktivitetApi.velgAktivitet(aktivitetsmalId = "123", orgnr = enVirksomhet.orgnr, block = withToken())
                .body<ValgtAktivitetDTO>()

            aktivitet.fullført shouldBe false

            aktivitetApi.fullførAktivitet(id = aktivitet.id, orgnr = aktivitet.valgtAv.orgnr, block = withToken())
            val aktivitetEtterFullfør =
                aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr, block = withToken())
                    .body<List<ValgtAktivitetDTO>>().filter { it.id == aktivitet.id }.single()

            aktivitetEtterFullfør.fullført shouldBe true
        }
    }

    @Test
    fun `skal ikke kunne sette en aktivitet til fullført i feil organisasjon`() {
        runBlocking {
            val aktivitet = aktivitetApi.velgAktivitet(aktivitetsmalId = "123", orgnr = enVirksomhet.orgnr, block = withToken())
                .body<ValgtAktivitetDTO>()

            aktivitet.fullført shouldBe false

            aktivitetApi.fullførAktivitet(id = aktivitet.id, orgnr = "999999999", block = withToken())
                .status shouldBe HttpStatusCode.Forbidden
        }
    }
}
