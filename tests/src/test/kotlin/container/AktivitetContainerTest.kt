package container

import api.dto.ValgtAktivitetDTO
import container.helper.TestContainerHelper
import container.helper.withToken
import enVirksomhet
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.toKotlinLocalDate
import request.AktivitetApi
import java.time.LocalDate
import kotlin.test.Test

class AktivitetContainerTest {
    private val aktivitetApi = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    fun `skal kunne hente valgte aktiviteter`() {
        runBlocking {
            val aktivitetsIder = listOf(
                aktivitetApi.velgAktivitet(aktivitetsmalId = "123", orgnr = enVirksomhet.orgnr, block = withToken())
                    .body<ValgtAktivitetDTO>().id,
                aktivitetApi.velgAktivitet(aktivitetsmalId = "1234", orgnr = enVirksomhet.orgnr, block = withToken())
                    .body<ValgtAktivitetDTO>().id
            )
            val resultat = aktivitetApi.hentValgteAktiviteterForVirksomhet(
                orgnr = enVirksomhet.orgnr,
                withToken()
            )
            resultat.status shouldBe HttpStatusCode.OK
            resultat.body<List<ValgtAktivitetDTO>>().map { it.id } shouldContainAll aktivitetsIder
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
    fun `skal kunne velge en aktivitet uten frist`() {
        runBlocking {
            val aktivitet = aktivitetApi.velgAktivitet(aktivitetsmalId = "123", orgnr = enVirksomhet.orgnr, block = withToken())
                .body<ValgtAktivitetDTO>()

            val hentetAktivitet =
                aktivitetApi.hentValgtAktivitet(orgnr = enVirksomhet.orgnr, aktivitetsId = aktivitet.id, block = withToken())
                    .body<ValgtAktivitetDTO>()

            aktivitet shouldBe hentetAktivitet
            aktivitet.frist shouldBe null
        }
    }

    @Test
    fun `skal kunne velge en aktivitet med frist`() {
        runBlocking {
            val idag = LocalDate.now().toKotlinLocalDate()
            val aktivitet = aktivitetApi.velgAktivitet(aktivitetsmalId = "123", frist = idag, orgnr = enVirksomhet.orgnr, block = withToken())
                .body<ValgtAktivitetDTO>()

            val hentetAktivitet =
                aktivitetApi.hentValgtAktivitet(orgnr = enVirksomhet.orgnr, aktivitetsId = aktivitet.id, block = withToken())
                    .body<ValgtAktivitetDTO>()

            aktivitet shouldBe hentetAktivitet
            aktivitet.frist shouldNotBe null
            aktivitet.frist!! shouldBeEqualComparingTo idag
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
    fun `skal kunne sette en valgt aktivitet til fullført`() {
        runBlocking {
            val aktivitet = aktivitetApi.velgAktivitet(aktivitetsmalId = "123", orgnr = enVirksomhet.orgnr, block = withToken())
                .body<ValgtAktivitetDTO>()
            aktivitet.fullført shouldBe false
            aktivitet.fullførtTidspunkt shouldBe null

            val aktivitetEtterFullfør = aktivitetApi.fullførAktivitet(
                aktivitetsId = aktivitet.id,
                aktivitetsmalId = aktivitet.aktivitetsmalId,
                orgnr = aktivitet.valgtAv.orgnr,
                block = withToken()).body<ValgtAktivitetDTO>()
            aktivitetEtterFullfør.fullført shouldBe true
            aktivitetEtterFullfør.fullførtTidspunkt shouldNotBe null
            aktivitetEtterFullfør.fullførtTidspunkt!! shouldBeGreaterThan Clock.System.now().minus(1, DateTimeUnit.MINUTE)
        }
    }

    @Test
    fun `skal kunne sette en ny aktivitet til fullført`() {
        runBlocking {
            val aktivitetEtterFullfør = aktivitetApi.fullførAktivitet(
                aktivitetsId = null,
                aktivitetsmalId = "123",
                orgnr = enVirksomhet.orgnr,
                block = withToken()).body<ValgtAktivitetDTO>()
            aktivitetEtterFullfør.fullført shouldBe true
            aktivitetEtterFullfør.fullførtTidspunkt shouldNotBe null
            aktivitetEtterFullfør.fullførtTidspunkt!! shouldBeGreaterThan Clock.System.now().minus(1, DateTimeUnit.MINUTE)
        }
    }

    @Test
    fun `skal ikke kunne sette en aktivitet til fullført i feil organisasjon`() {
        runBlocking {
            val aktivitet = aktivitetApi.velgAktivitet(aktivitetsmalId = "123", orgnr = enVirksomhet.orgnr, block = withToken())
                .body<ValgtAktivitetDTO>()

            aktivitet.fullført shouldBe false

            aktivitetApi.fullførAktivitet(aktivitetsId = aktivitet.id, aktivitetsmalId = aktivitet.aktivitetsmalId, orgnr = "999999999", block = withToken())
                .status shouldBe HttpStatusCode.Forbidden
        }
    }
}
