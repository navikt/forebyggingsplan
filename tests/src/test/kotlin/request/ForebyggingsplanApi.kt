package request

import api.endepunkt.ORGANISASJONER_PATH
import container.helper.TestContainerHelper.Companion.performGet
import container.helper.TestContainerHelper.Companion.performPost
import io.ktor.client.request.HttpRequestBuilder
import org.testcontainers.containers.GenericContainer

class ForebyggingsplanApi(
    private val forebyggingsplanContainer: GenericContainer<*>,
) {
    internal suspend fun hentVirksomheter(block: HttpRequestBuilder.() -> Unit = {}) =
        forebyggingsplanContainer.performGet(ORGANISASJONER_PATH, block)

    internal suspend fun oppdater(
        orgnr: String,
        aktivitetId: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = forebyggingsplanContainer.performPost("/aktivitet/$aktivitetId/orgnr/$orgnr/oppdater", block)
}
