package request

import api.endepunkt.AKTIVITETSMALER_PATH
import api.endepunkt.VALGTE_PATH
import container.helper.TestContainerHelper.Companion.performGet
import container.helper.TestContainerHelper.Companion.performPost
import io.ktor.client.request.setBody
import org.testcontainers.containers.GenericContainer

class AktivitetApi(private val forebyggingsplanContainer: GenericContainer<*>) {
    internal suspend fun hentAktiviteter() =
        forebyggingsplanContainer.performGet(AKTIVITETSMALER_PATH)

    internal suspend fun hentValgteAktiviteterForVirksomhet(orgnr: String) =
        forebyggingsplanContainer.performGet("$VALGTE_PATH/$orgnr")

    internal suspend fun velgAktivitet(aktivitetsmalId: String, orgnr: String) =
        forebyggingsplanContainer.performPost(VALGTE_PATH) {
            setBody(
                """
                {
                    "aktivitetsmalId": "$aktivitetsmalId",
                    "orgnr": "$orgnr"
                }
            """.trimIndent()
            )
        }
}

