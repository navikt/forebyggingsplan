package request

import api.endepunkt.FULLFØR_PATH
import api.endepunkt.ORGANISASJONER_PATH
import api.endepunkt.VALGTE_PATH
import container.helper.TestContainerHelper.Companion.performGet
import container.helper.TestContainerHelper.Companion.performPost
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import org.testcontainers.containers.GenericContainer
import kotlinx.datetime.LocalDate

class AktivitetApi(private val forebyggingsplanContainer: GenericContainer<*>) {

    internal suspend fun hentValgteAktiviteterForVirksomhet(orgnr: String, block: HttpRequestBuilder.() -> Unit = {}) =
        forebyggingsplanContainer.performGet("$VALGTE_PATH/$orgnr", block)

    internal suspend fun hentValgtAktivitet(orgnr:String, aktivitetsId: Int, block: HttpRequestBuilder.() -> Unit = {}) =
        forebyggingsplanContainer.performGet("$VALGTE_PATH/$orgnr/$aktivitetsId", block)

    internal suspend fun velgAktivitet(
        aktivitetsmalId: String,
        frist: LocalDate? = null,
        orgnr: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ) =
        forebyggingsplanContainer.performPost("$VALGTE_PATH/$orgnr") {
            apply(block)
            setBody(
                """
                {
                    "aktivitetsmalId": "$aktivitetsmalId"
                    "frist": $frist
                }
            """.trimIndent()
            )
        }

    internal suspend fun hentVirksomheter(block: HttpRequestBuilder.() -> Unit = {}) =
        forebyggingsplanContainer.performGet(ORGANISASJONER_PATH, block)

    internal suspend fun fullførAktivitet(aktivitetsId: Int?, aktivitetsmalId: String, orgnr: String, block: HttpRequestBuilder.() -> Unit = {}
    ) =
        forebyggingsplanContainer.performPost("$FULLFØR_PATH/$orgnr") {
            apply(block)
            setBody(
                """
                    {
                        "aktivitetsId": $aktivitetsId
                        "aktivitetsmalId": "$aktivitetsmalId"
                    }
                """.trimIndent()
            )
        }
}

