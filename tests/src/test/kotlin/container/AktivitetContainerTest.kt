package container

import api.dto.AktivitetsmalDTO
import api.dto.ValgtAktivitetDTO
import container.helper.TestContainerHelper
import container.helper.withToken
import domene.enVirksomhet
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import request.AktivitetApi
import java.util.UUID
import kotlin.test.Test

class AktivitetContainerTest {
    private val aktivitetApi = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    fun `skal kunne hente alle aktiviteter`() {
        runBlocking {
            aktivitetApi.hentAktivitetsmaler(withToken()).body<List<AktivitetsmalDTO>>().size shouldBeGreaterThanOrEqual 1
        }
    }

    @Test
    fun `skal kunne fullføre en aktivitet`() {
        runBlocking {
            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr, withToken())
                .body<List<ValgtAktivitetDTO>>().shouldBeEmpty()
            val aktivitetSomSkalVelges =
                aktivitetApi.hentAktivitetsmaler(withToken()).body<List<AktivitetsmalDTO>>().first()
            val valgtAktivitetDto = aktivitetApi.velgAktivitet(
                aktivitetsmalId = aktivitetSomSkalVelges.id,
                orgnr = enVirksomhet.orgnr,
                withToken()
            ).body<ValgtAktivitetDTO>()
            aktivitetApi
                .fullførAktivitet(id = valgtAktivitetDto.id, orgnr = valgtAktivitetDto.valgtAv.orgnr, block = withToken())
                .status shouldBe HttpStatusCode.OK
            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr, withToken())
                .body<List<ValgtAktivitetDTO>>().find { it.id == valgtAktivitetDto.id }!!.fullført shouldBe true
        }
    }

    @Test
    fun `skal ikke kunne fullføre aktiviteter som virksomheten ikke har tilgang til`() {
        runBlocking {
            val aktivitetSomSkalVelges =
                aktivitetApi.hentAktivitetsmaler(withToken()).body<List<AktivitetsmalDTO>>().random()
            val valgtAktivitetDto = aktivitetApi.velgAktivitet(
                aktivitetsmalId = aktivitetSomSkalVelges.id,
                orgnr = enVirksomhet.orgnr,
                withToken()
            ).body<ValgtAktivitetDTO>()
            aktivitetApi
                .fullførAktivitet(id = valgtAktivitetDto.id, orgnr = "000000000", block = withToken())
                .status shouldBe HttpStatusCode.Forbidden
        }
    }

    @Test
    fun `skal kunne hente og velge en aktivitet`() {
        runBlocking {
            val aktivitetSomSkalVelges =
                aktivitetApi.hentAktivitetsmaler(withToken()).body<List<AktivitetsmalDTO>>().first()
            val valgtAktivitetDto = aktivitetApi.velgAktivitet(
                aktivitetsmalId = aktivitetSomSkalVelges.id,
                orgnr = enVirksomhet.orgnr,
                withToken()
            ).body<ValgtAktivitetDTO>()
            valgtAktivitetDto.aktivitetsmalId shouldBeEqualToComparingFields aktivitetSomSkalVelges
            val alleValgteAktiviteter =
                aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr, withToken())
                    .body<List<ValgtAktivitetDTO>>()
            alleValgteAktiviteter.size shouldBeGreaterThanOrEqual 1
            alleValgteAktiviteter.forAtLeastOne {
                it.aktivitetsmalId shouldBe aktivitetSomSkalVelges.id
                it.opprettelsesTidspunkt.toLocalDateTime(TimeZone.currentSystemDefault()).date shouldBe Clock.System.todayIn(
                    TimeZone.currentSystemDefault()
                )
            }
        }
    }

    @Test
    fun `skal få 404 dersom man ikke finner en aktivitet`() {
        runBlocking {
            aktivitetApi.velgAktivitet(
                aktivitetsmalId = UUID.randomUUID().toString(),
                orgnr = enVirksomhet.orgnr,
                withToken()
            ).status shouldBe HttpStatusCode.NotFound
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
}
