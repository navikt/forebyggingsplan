package api.endepunkt

import AktivitetService
import domene.Aktivitet
import domene.Virksomhet
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.aktivitetEndepunkter(aktivitetService: AktivitetService) {
    val AKTIVITET_PATH = "aktiviteter"

    val queryParameters = object {
        val orgnr ="orgnummer"
    }

    get("/$AKTIVITET_PATH") {
        call.respond(aktivitetService.hentAktiviteter().map(Aktivitet::tilDto))
    }

    get("/$AKTIVITET_PATH/{${queryParameters.orgnr}}") {
        val orgnr = call.parameters[queryParameters.orgnr] ?: return@get call.respond(HttpStatusCode.NotFound)
        val virksomhet = Virksomhet(orgnr = orgnr)
        call.respond(aktivitetService.hentAktiviteterForVirksomhet(virksomhet).map(Aktivitet::tilDto))
    }
}
