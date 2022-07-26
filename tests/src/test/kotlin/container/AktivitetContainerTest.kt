package container

import api.dto.AktivitetDTO
import api.endepunkt.AKTIVITET_PATH
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.serialization.responseObject
import container.TestContainerHelper.Companion.performGet
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
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
        val orgnr = "123456789"
        hentAktiviteterForVirksomhet(orgnr = orgnr).size shouldBeGreaterThanOrEqual 1
    }

    private fun hentAktiviteterForVirksomhet(orgnr: String): List<AktivitetDTO> {
        return forebyggingsplanContainer.performGet("$AKTIVITET_PATH/$orgnr")
            .tilListeRespons<AktivitetDTO>()
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
