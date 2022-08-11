import api.endepunkt.aktivitetEndepunkter
import api.endepunkt.helseEndepunkter
import db.AktivitetRepository
import exceptions.IkkeFunnetException
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    bootstrapServer()
}

fun bootstrapServer() {
    val aktivitetService = AktivitetService(aktivitetRepository = AktivitetRepository())

    embeddedServer(factory = Netty, port = System.getenv("SERVER_PORT")?.toInt() ?: 8080) {
        install(ContentNegotiation) {
            json()
        }
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                when (cause) {
                    is IkkeFunnetException -> call.respond(status = HttpStatusCode.NotFound, message = cause.message!!)
                    else -> {
                        this@embeddedServer.log.error("Uhåndtert feil", cause)
                        call.respond(status = HttpStatusCode.InternalServerError, "Uhåndtert feil")
                    }
                }
            }
        }
        routing {
            helseEndepunkter()
            aktivitetEndepunkter(aktivitetService = aktivitetService)
        }
    }.start(wait = true)
}

