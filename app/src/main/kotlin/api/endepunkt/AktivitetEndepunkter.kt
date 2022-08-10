package api.endepunkt

import AktivitetService
import domene.Aktivitet
import domene.ValgtAktivitet
import domene.Virksomhet
import domene.enArbeidsgiverRepresentant
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val AKTIVITET_PATH = "aktiviteter"

fun Route.aktivitetEndepunkter(aktivitetService: AktivitetService) {

    val parameters = object {
        val orgnr ="orgnummer"
        val aktivitetsId ="aktivitetsId"
    }

    get("/$AKTIVITET_PATH") {
        call.respond(aktivitetService.hentAktiviteter().map(Aktivitet::tilDto))
    }

//    get("/$AKTIVITET_PATH/{${queryParameters.orgnr}}") {
//        val orgnr = call.parameters[queryParameters.orgnr] ?: return@get call.respond(HttpStatusCode.NotFound)
//        val virksomhet = Virksomhet(orgnr = orgnr)
//        call.respond(aktivitetService.hentAktiviteterForVirksomhet(virksomhet).map(Aktivitet::tilDto))
//    }

    post("/$AKTIVITET_PATH/{${parameters.aktivitetsId}}") {
        val aktivitetsId = call.parameters[parameters.aktivitetsId] ?: return@post call.respond(HttpStatusCode.NotFound)
        val valgtAktivitet = aktivitetService.velgAktivitet(
            aktivitetsId = aktivitetsId,
            arbeidsgiverRepresentant = enArbeidsgiverRepresentant
        )
        call.respond(valgtAktivitet.tilDto())
    }

    get("/$AKTIVITET_PATH/{${parameters.orgnr}}") {
        val orgnr = call.parameters[parameters.orgnr] ?: return@get call.respond(HttpStatusCode.NotFound)
        val virksomhet = Virksomhet(orgnr = orgnr)
        call.respond(aktivitetService.hentValgteAktiviteterForVirksomhet(virksomhet).map(ValgtAktivitet::tilDto))
    }
}
