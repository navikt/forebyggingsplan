package api.endepunkt

import AktivitetService
import domene.Aktivitet
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val AKTIVITET_PATH = "aktiviteter"

fun Route.aktivitetEndepunkter(aktivitetService: AktivitetService) {

//    val queryParameters = object {
//        val orgnr ="orgnummer"
//    }

    get("/$AKTIVITET_PATH") {
        call.respond(aktivitetService.hentAktiviteter().map(Aktivitet::tilDto))
    }

//    get("/$AKTIVITET_PATH/{${queryParameters.orgnr}}") {
//        val orgnr = call.parameters[queryParameters.orgnr] ?: return@get call.respond(HttpStatusCode.NotFound)
//        val virksomhet = Virksomhet(orgnr = orgnr)
//        call.respond(aktivitetService.hentAktiviteterForVirksomhet(virksomhet).map(Aktivitet::tilDto))
//    }
}
