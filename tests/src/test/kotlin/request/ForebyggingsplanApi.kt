package request

import api.dto.ValgtAktivitetDTO
import api.endepunkt.ENDRE_FRIST_PATH
import api.endepunkt.FULLFØR_PATH
import api.endepunkt.ORGANISASJONER_PATH
import api.endepunkt.VALGTE_PATH
import container.helper.TestContainerHelper.Companion.performGet
import container.helper.TestContainerHelper.Companion.performPost
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.datetime.LocalDate
import org.testcontainers.containers.GenericContainer

class ForebyggingsplanApi(private val forebyggingsplanContainer: GenericContainer<*>) {

    internal suspend fun hentValgteAktiviteterForVirksomhet(
        orgnr: String,
        block: HttpRequestBuilder.() -> Unit = {}

    ) = forebyggingsplanContainer.performGet("$VALGTE_PATH/$orgnr", block)

    internal suspend fun hentValgtAktivitet(
        orgnr: String,
        aktivitetsId: Int,
        block: HttpRequestBuilder.() -> Unit = {}
    ) =
        forebyggingsplanContainer.performGet("$VALGTE_PATH/$orgnr", block)
            .body<List<ValgtAktivitetDTO>>().first { it.id == aktivitetsId }

    internal suspend fun velgAktivitet(
        aktivitetsmalId: String, frist: LocalDate? = null, orgnr: String, block: HttpRequestBuilder.() -> Unit = {}
    ) = velgAktivitetMedTekstFrist(
        aktivitetsmalId,
        frist?.toString(),
        orgnr,
        block
    )

    internal suspend fun velgAktivitetMedTekstFrist(
        aktivitetsmalId: String, frist: String?, orgnr: String, block: HttpRequestBuilder.() -> Unit = {}
    ) = forebyggingsplanContainer.performPost("$VALGTE_PATH/$orgnr") {
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

    internal suspend fun fullførAktivitet(
        aktivitetsId: Int?,
        aktivitetsmalId: String,
        orgnr: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ) = forebyggingsplanContainer.performPost("$FULLFØR_PATH/$orgnr") {
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

    internal suspend fun oppdaterFristPåAktivitet(
        frist: LocalDate?,
        aktivitetsId: Int?,
        aktivitetsmalId: String,
        orgnr: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ) = forebyggingsplanContainer.performPost("$VALGTE_PATH/$orgnr/$ENDRE_FRIST_PATH") {
        apply(block)
        setBody(
            """
                    {
                        "aktivitetsId": $aktivitetsId
                        "aktivitetsmalId": "$aktivitetsmalId"
                        "frist": $frist
                    }
                """.trimIndent()
        )
    }

    internal suspend fun hentFullførteAktiviteter(
        orgnr: String, block: HttpRequestBuilder.() -> Unit = {}
    ) = forebyggingsplanContainer.performGet("/aktiviteter/orgnr/$orgnr/fullforte", block)

    internal suspend fun fullførAktivitet(
        aktivitetsId: String, aktivitetsversjon: String, orgnr: String, block: HttpRequestBuilder.() -> Unit = {}
    ) =
        forebyggingsplanContainer.performPost(
            "/aktivitet/$aktivitetsId/versjon/$aktivitetsversjon/orgnr/$orgnr/fullfor",
            block
        )

    internal suspend fun oppdater(
        orgnr: String, aktivitetId: String, block: HttpRequestBuilder.() -> Unit = {}
    ) = forebyggingsplanContainer.performPost("/aktivitet/$aktivitetId/orgnr/$orgnr/oppdater", block)
}
