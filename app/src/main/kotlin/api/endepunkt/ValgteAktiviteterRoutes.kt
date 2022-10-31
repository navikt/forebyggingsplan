package api.endepunkt

import AktivitetService
import domene.ValgtAktivitet
import domene.Virksomhet
import exceptions.UgyldigForespørselException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

const val ORGNR = "orgnr"
const val VALGTE_PATH = "valgteaktiviteter"
const val FULLFØRTE_PATH = "fullforteaktiviteter"

fun Route.valgteAktiviteter(aktivitetService: AktivitetService) {
    post("/$VALGTE_PATH") {
        // FIXME
        call.respond("OK")
    }

    get("/$VALGTE_PATH/{$ORGNR}") {
        call.respond(aktivitetService.hentValgteAktiviteterForVirksomhet(call.virksomhet).map(ValgtAktivitet::tilDto))
    }
}


fun Route.fullførteAktiviteter(aktivitetService: AktivitetService) {
    get("/$VALGTE_PATH/{$ORGNR}/$FULLFØRTE_PATH") {
        call.respond(aktivitetService.hentFullførteAktiviteterForVirksomhet(call.virksomhet)
            .map(ValgtAktivitet::tilDto))
    }
    post("/$VALGTE_PATH/{$ORGNR}/$FULLFØRTE_PATH/{aktivitetId}") {
        // FIXME
        call.respond(status = HttpStatusCode.OK, message = "")
    }
}

val ApplicationCall.virksomhet get() = Virksomhet(this.orgnr)
val ApplicationCall.orgnr get() = this.parameters[ORGNR] ?: throw UgyldigForespørselException("Manglende parameter 'orgnr'")

