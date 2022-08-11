package container

import api.dto.AktivitetDTO
import api.dto.ValgtAktivitetDTO
import api.endepunkt.AKTIVITET_PATH
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
        hentValgteAktiviteterForVirksomhet(enVirksomhet.orgnr).data.shouldBeEmpty()
        val aktivitetSomSkalVelges = hentAktiviteter().data.first()
        val valgtAktivitetDto = velgAktivitet(aktivitetSomSkalVelges.id).third.get()
        valgtAktivitetDto.aktivitet shouldBeEqualToComparingFields aktivitetSomSkalVelges
        val alleValgteAktiviteter = hentValgteAktiviteterForVirksomhet(enVirksomhet.orgnr).data
        alleValgteAktiviteter.size shouldBeGreaterThanOrEqual 1
        alleValgteAktiviteter.forAtLeastOne {
            it.aktivitet.id shouldBe aktivitetSomSkalVelges.id
        }
    }

    @Test
    fun `skal få 404 dersom man ikke finner en aktivitet`() {
        velgAktivitet("yololoooo").second.statusCode shouldBe 404
    }

    private fun hentAktiviteter() = forebyggingsplanContainer.performGet(AKTIVITET_PATH)
        .tilListeRespons<AktivitetDTO>()

    private fun hentValgteAktiviteterForVirksomhet(orgnr: String) =
        forebyggingsplanContainer.performGet("$AKTIVITET_PATH/$orgnr")
            .tilListeRespons<ValgtAktivitetDTO>()

    private fun velgAktivitet(aktivitetsId: String) =
        forebyggingsplanContainer.performPost("$AKTIVITET_PATH/$aktivitetsId")
            .tilSingelRespons<ValgtAktivitetDTO>()
}
