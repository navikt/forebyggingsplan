package api.endepunkt

import AktivitetService
import domene.Aktivitetsmal
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val AKTIVITETSMALER_PATH = "aktivitetsmaler"
const val AKTIVITETSMAL_ID ="aktivitetsmalId"

fun Route.aktivitetsmaler(aktivitetService: AktivitetService) {
    get("/$AKTIVITETSMALER_PATH") {
        call.respond(aktivitetService.hentAktivitetsmaler().map(Aktivitetsmal::tilDto))
    }
}


