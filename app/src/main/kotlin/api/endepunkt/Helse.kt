package api.endepunkt

import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.helseEndepunkter() {
    get("/internal/isAlive") {
        call.respond("ALIVE")
    }
    get("/internal/isReady") {
        call.respond("READY")
    }
}
