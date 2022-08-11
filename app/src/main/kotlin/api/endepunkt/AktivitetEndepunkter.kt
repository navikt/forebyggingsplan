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

val AKTIVITETER_PATH = "aktiviteter"
val VALGTE_PATH = "valgte"
val FULLFØRTE_PATH = "fullførte"


fun Route.aktivitetEndepunkter(aktivitetService: AktivitetService) {

    val parameters = object {
        val orgnr ="orgnummer"
        val aktivitetsId ="aktivitetsId"
    }

    get("/$AKTIVITETER_PATH") {
        call.respond(aktivitetService.hentAktiviteter().map(Aktivitet::tilDto))
    }

    post("/$AKTIVITETER_PATH/{${parameters.orgnr}}/$VALGTE_PATH/{${parameters.aktivitetsId}}") {
        val aktivitetsId = call.parameters[parameters.aktivitetsId] ?: return@post call.respond(HttpStatusCode.NotFound)
        val valgtAktivitet = aktivitetService.velgAktivitet(
            aktivitetsId = aktivitetsId,
            arbeidsgiverRepresentant = enArbeidsgiverRepresentant
        )
        call.respond(valgtAktivitet.tilDto())
    }

    get("/$AKTIVITETER_PATH/{${parameters.orgnr}}/$VALGTE_PATH") {
        val orgnr = call.parameters[parameters.orgnr] ?: return@get call.respond(HttpStatusCode.NotFound)
        val virksomhet = Virksomhet(orgnr = orgnr)
        call.respond(aktivitetService.hentValgteAktiviteterForVirksomhet(virksomhet).map(ValgtAktivitet::tilDto))
    }

    post("/$AKTIVITETER_PATH/{${parameters.orgnr}}/$FULLFØRTE_PATH/{${parameters.aktivitetsId}}") {
        call.respond(status = HttpStatusCode.NotImplemented, message = "")
    }

    get("/$AKTIVITETER_PATH/{${parameters.orgnr}}/$FULLFØRTE_PATH") {
        call.respond(status = HttpStatusCode.NotImplemented, message = "")
    }
}
