package api.endepunkt

import AktivitetService
import domene.Aktivitet
import domene.Virksomhet
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.aktivitetEndepunkter(aktivitetService: AktivitetService) {

    val queryParameters = object {
        val orgnr ="orgnummer"
    }

    get("/aktiviteter") {
        call.respond(aktivitetService.hentAktiviteter().map(Aktivitet::tilDto))
    }

    get("/aktiviteter/{${queryParameters.orgnr}}") {
        val orgnr = call.parameters[queryParameters.orgnr] ?: return@get call.respond(HttpStatusCode.NotFound)
        val virksomhet = Virksomhet(orgnr = orgnr)
        call.respond(aktivitetService.hentAktiviteterForVirksomhet(virksomhet).map(Aktivitet::tilDto))
    }
}
