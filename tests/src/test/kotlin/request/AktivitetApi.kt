package request

import api.dto.AktivitetsmalDTO
import api.dto.ValgtAktivitetDTO
import api.endepunkt.AKTIVITETSMALER_PATH
import api.endepunkt.VALGTE_PATH
import com.github.kittinunf.fuel.core.extensions.jsonBody
import container.helper.TestContainerHelper.Companion.performGet
import container.helper.TestContainerHelper.Companion.performPost
import org.testcontainers.containers.GenericContainer
import tilListeRespons
import tilSingelRespons

class AktivitetApi(private val forebyggingsplanContainer: GenericContainer<*>) {
    internal fun hentAktiviteter() = forebyggingsplanContainer.performGet(AKTIVITETSMALER_PATH)
        .tilListeRespons<AktivitetsmalDTO>()

    internal fun hentValgteAktiviteterForVirksomhet(orgnr: String) =
        forebyggingsplanContainer.performGet("$VALGTE_PATH/$orgnr")
            .tilListeRespons<ValgtAktivitetDTO>()

    internal fun velgAktivitet(aktivitetsmalId: String, orgnr: String) =
        forebyggingsplanContainer.performPost(VALGTE_PATH)
            .jsonBody("""
                {
                    "aktivitetsmalId": "$aktivitetsmalId",
                    "orgnr": "$orgnr"
                }
            """.trimIndent())
            .tilSingelRespons<ValgtAktivitetDTO>()
}

