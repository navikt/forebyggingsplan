package container

import container.helper.TestContainerHelper
import data
import domene.enVirksomhet
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import request.AktivitetApi
import kotlin.test.Test

class AktivitetContainerTest {
    private val aktivitetApi = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    fun `skal kunne hente alle aktiviteter`() {
        aktivitetApi.hentAktiviteter().data.size shouldBeGreaterThanOrEqual 1
    }

    @Test
    fun `skal kunne hente og velge en aktivitet`() {
        aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr).data.shouldBeEmpty()
        val aktivitetSomSkalVelges = aktivitetApi.hentAktiviteter().data.first()
        val valgtAktivitetDto = aktivitetApi.velgAktivitet(aktivitetsmalId = aktivitetSomSkalVelges.id, orgnr = enVirksomhet.orgnr).third.get()
        valgtAktivitetDto.aktivitetsmalId shouldBeEqualToComparingFields aktivitetSomSkalVelges
        val alleValgteAktiviteter = aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr).data
        alleValgteAktiviteter.size shouldBeGreaterThanOrEqual 1
        alleValgteAktiviteter.forAtLeastOne {
            it.aktivitetsmalId shouldBe aktivitetSomSkalVelges.id
        }
    }

    @Test
    fun `skal få 404 dersom man ikke finner en aktivitet`() {
        aktivitetApi.velgAktivitet(aktivitetsmalId = "yololoooo", orgnr = enVirksomhet.orgnr).second.statusCode shouldBe 404
    }


}
