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
import request.AktivitetApi
import kotlin.test.Test

class AktivitetContainerTest {
    private val aktivitetApi = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    fun `skal kunne hente alle aktiviteter`() {
        runBlocking {
            aktivitetApi.hentAktiviteter(withToken()).body<List<AktivitetsmalDTO>>().size shouldBeGreaterThanOrEqual 1
        }
    }

    @Test
    fun `skal kunne hente og velge en aktivitet`() {
        runBlocking {
            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr, withToken())
                .body<List<ValgtAktivitetDTO>>().shouldBeEmpty()
            val aktivitetSomSkalVelges =
                aktivitetApi.hentAktiviteter(withToken()).body<List<AktivitetsmalDTO>>().first()
            val valgtAktivitetDto = aktivitetApi.velgAktivitet(aktivitetsmalId = aktivitetSomSkalVelges.id,
                orgnr = enVirksomhet.orgnr,
                withToken()).body<ValgtAktivitetDTO>()
            valgtAktivitetDto.aktivitetsmalId shouldBeEqualToComparingFields aktivitetSomSkalVelges
            val alleValgteAktiviteter =
                aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr, withToken())
                    .body<List<ValgtAktivitetDTO>>()
            alleValgteAktiviteter.size shouldBeGreaterThanOrEqual 1
            alleValgteAktiviteter.forAtLeastOne {
                it.aktivitetsmalId shouldBe aktivitetSomSkalVelges.id
            }
        }

    }

    @Test
    fun `skal få 404 dersom man ikke finner en aktivitet`() {
        runBlocking {
            aktivitetApi.velgAktivitet(aktivitetsmalId = "yololoooo",
                orgnr = enVirksomhet.orgnr,
                withToken()).status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `skal få 403 Forbidden dersom man ikke har tilgang i Altinn`() {

        runBlocking {
            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr,
                withToken()).status shouldBe HttpStatusCode.Forbidden
        }
    }
}
