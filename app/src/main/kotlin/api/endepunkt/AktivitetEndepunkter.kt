package api.endepunkt

import domene.Aktivitet
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.aktivitetEndepunkter() {
    get("/aktiviteter") {
        call.respond(domene.aktiviteter.map(Aktivitet::tilDto))
    }
}