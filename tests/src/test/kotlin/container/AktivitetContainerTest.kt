package container

import api.dto.AktivitetDTO
import api.dto.ValgtAktivitetDTO
import api.endepunkt.AKTIVITET_PATH
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.serialization.responseObject
import container.TestContainerHelper.Companion.performGet
import container.TestContainerHelper.Companion.performPost
import domene.enArbeidsgiverRepresentant
import domene.enVirksomhet
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.fail

class AktivitetContainerTest {
    private val forebyggingsplanContainer = TestContainerHelper.forebyggingsplanContainer

    @Test
    fun `skal kunne hente alle aktiviteter`() {
        hentAktiviteter().size shouldBeGreaterThanOrEqual 1
    }

    @Test
    fun `skal kunne hente velge en aktivitet`() {
        hentValgteAktiviteterForVirksomhet(enVirksomhet.orgnr).shouldBeEmpty()
        val alleAktiviteter = hentAktiviteter()
        val aktivitetSomSkalVelges = alleAktiviteter.first()
        velgAktivitet(aktivitetSomSkalVelges.id)
        val alleValgteAktiviteter = hentValgteAktiviteterForVirksomhet(enVirksomhet.orgnr)
        alleValgteAktiviteter.size shouldBeGreaterThanOrEqual 1
        alleValgteAktiviteter.forAtLeastOne {
            it.aktivitet.id shouldBe aktivitetSomSkalVelges.id
        }
    }

    private fun hentAktiviteter(): List<AktivitetDTO> {
        return forebyggingsplanContainer.performGet(AKTIVITET_PATH)
            .tilListeRespons<AktivitetDTO>()
            .third.fold(
                success = { respons -> respons },
                failure = { fail(it.message) }
            )
        }

    private fun hentValgteAktiviteterForVirksomhet(orgnr: String): List<ValgtAktivitetDTO> {
        return forebyggingsplanContainer.performGet("$AKTIVITET_PATH/$orgnr")
            .tilListeRespons<ValgtAktivitetDTO>()
            .third.fold(
                success = { respons -> respons },
                failure = { fail(it.message) }
            )
        }

    private fun velgAktivitet(aktivitetsId: String): ValgtAktivitetDTO {
        return forebyggingsplanContainer.performPost("$AKTIVITET_PATH/$aktivitetsId")
            .tilSingelRespons<ValgtAktivitetDTO>()
            .third.fold(
                success = { respons -> respons },
                failure = { fail(it.message) }
            )
        }


    @OptIn(InternalSerializationApi::class)
    inline fun <reified T : Any> Request.tilSingelRespons() =
        this.responseObject(loader = T::class.serializer(), json = Json.Default)

    @OptIn(InternalSerializationApi::class)
    inline fun <reified T : Any> Request.tilListeRespons() =
        this.responseObject(loader = ListSerializer(T::class.serializer()), json = Json.Default)

}
