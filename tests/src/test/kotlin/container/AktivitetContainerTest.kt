package container

import api.dto.AktivitetDTO
import api.dto.ValgtAktivitetDTO
import api.endepunkt.AKTIVITETER_PATH
import api.endepunkt.VALGTE_PATH
import container.TestContainerHelper.Companion.performGet
import container.TestContainerHelper.Companion.performPost
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
        val valgtAktivitetDto = velgAktivitet(aktivitetsId = aktivitetSomSkalVelges.id, orgnr = enVirksomhet.orgnr).third.get()
        valgtAktivitetDto.aktivitet shouldBeEqualToComparingFields aktivitetSomSkalVelges
        val alleValgteAktiviteter = hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr).data
        alleValgteAktiviteter.size shouldBeGreaterThanOrEqual 1
        alleValgteAktiviteter.forAtLeastOne {
            it.aktivitet.id shouldBe aktivitetSomSkalVelges.id
        }
    }

    @Test
    fun `skal få 404 dersom man ikke finner en aktivitet`() {
        velgAktivitet(aktivitetsId = "yololoooo", orgnr = enVirksomhet.orgnr).second.statusCode shouldBe 404
    }

    private fun hentAktiviteter() = forebyggingsplanContainer.performGet(AKTIVITETER_PATH)
        .tilListeRespons<AktivitetDTO>()

    private fun hentValgteAktiviteterForVirksomhet(orgnr: String) =
        forebyggingsplanContainer.performGet("$AKTIVITETER_PATH/$orgnr/$VALGTE_PATH")
            .tilListeRespons<ValgtAktivitetDTO>()

    private fun velgAktivitet(aktivitetsId: String, orgnr: String) =
        forebyggingsplanContainer.performPost("$AKTIVITETER_PATH/$orgnr/$VALGTE_PATH/$aktivitetsId")
            .tilSingelRespons<ValgtAktivitetDTO>()
}
