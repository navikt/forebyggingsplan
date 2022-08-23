package request

import api.endepunkt.AKTIVITETSMALER_PATH
import api.endepunkt.VALGTE_PATH
import container.helper.TestContainerHelper.Companion.performGet
import container.helper.TestContainerHelper.Companion.performPost
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import org.testcontainers.containers.GenericContainer

class AktivitetApi(private val forebyggingsplanContainer: GenericContainer<*>) {
    internal suspend fun hentAktiviteter(block: HttpRequestBuilder.() -> Unit = {}) =
        forebyggingsplanContainer.performGet(AKTIVITETSMALER_PATH, block)

    internal suspend fun hentValgteAktiviteterForVirksomhet(orgnr: String, block: HttpRequestBuilder.() -> Unit = {}) =
        forebyggingsplanContainer.performGet("$VALGTE_PATH/$orgnr", block)

    internal suspend fun velgAktivitet(aktivitetsmalId: String, orgnr: String, block: HttpRequestBuilder.() -> Unit = {}) =
        forebyggingsplanContainer.performPost(VALGTE_PATH) {
            apply(block)
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

