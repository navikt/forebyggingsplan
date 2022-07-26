import api.endepunkt.aktivitetEndepunkter
import api.endepunkt.helse
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

fun main() {
    bootstrap()
}

fun bootstrap() {
    embeddedServer(factory = Netty, port = System.getenv("SERVER_PORT")?.toInt() ?: 8080 ) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            aktivitetEndepunkter()
            helse()
        }
    }.start(wait = true)
}

