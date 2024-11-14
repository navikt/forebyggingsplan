package request

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

internal object TestHttpClient {
    internal val client = HttpClient(
        CIO.create {
            requestTimeout = 0
        },
    ) {
        install(ContentNegotiation) {
            json()
        }
    }
}
