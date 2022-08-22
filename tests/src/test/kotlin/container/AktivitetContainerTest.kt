package container

import api.dto.AktivitetsmalDTO
import api.dto.ValgtAktivitetDTO
import api.endepunkt.AKTIVITETSMALER_PATH
import api.endepunkt.VALGTE_PATH
import com.github.kittinunf.fuel.core.extensions.jsonBody
import container.helper.TestContainerHelper
import container.helper.TestContainerHelper.Companion.performGet
import container.helper.TestContainerHelper.Companion.performPost
import data
import domene.enVirksomhet
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import tilListeRespons
import tilSingelRespons
import kotlin.test.Test

class AktivitetContainerTest {
    private val forebyggingsplanContainer = TestContainerHelper.forebyggingsplanContainer

    @Test
    fun `skal kunne hente alle aktiviteter`() {
        hentAktiviteter().data.size shouldBeGreaterThanOrEqual 1
    }

    @Test
    fun `skal kunne hente og velge en aktivitet`() {
        hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr).data.shouldBeEmpty()
        val aktivitetSomSkalVelges = hentAktiviteter().data.first()
        val valgtAktivitetDto = velgAktivitet(aktivitetsmalId = aktivitetSomSkalVelges.id, orgnr = enVirksomhet.orgnr).third.get()
        valgtAktivitetDto.aktivitetsmalId shouldBeEqualToComparingFields aktivitetSomSkalVelges
        val alleValgteAktiviteter = hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr).data
        alleValgteAktiviteter.size shouldBeGreaterThanOrEqual 1
        alleValgteAktiviteter.forAtLeastOne {
            it.aktivitetsmalId shouldBe aktivitetSomSkalVelges.id
        }
    }

    @Test
    fun `skal få 404 dersom man ikke finner en aktivitet`() {
        velgAktivitet(aktivitetsmalId = "yololoooo", orgnr = enVirksomhet.orgnr).second.statusCode shouldBe 404
    }

    private fun hentAktiviteter() = forebyggingsplanContainer.performGet(AKTIVITETSMALER_PATH)
        .tilListeRespons<AktivitetsmalDTO>()

    private fun hentValgteAktiviteterForVirksomhet(orgnr: String) =
        forebyggingsplanContainer.performGet("$VALGTE_PATH/$orgnr")
            .tilListeRespons<ValgtAktivitetDTO>()

    private fun velgAktivitet(aktivitetsmalId: String, orgnr: String) =
        forebyggingsplanContainer.performPost(VALGTE_PATH)
            .jsonBody("""
                {
                    "aktivitetsmalId": "$aktivitetsmalId",
                    "orgnr": "$orgnr"
                }
            """.trimIndent())
            .tilSingelRespons<ValgtAktivitetDTO>()
}
