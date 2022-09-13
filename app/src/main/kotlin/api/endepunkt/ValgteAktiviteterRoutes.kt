package api.endepunkt

import AktivitetService
import api.dto.OpprettValgtAktivitetDTO
import domene.ValgtAktivitet
import domene.Virksomhet
import domene.enArbeidsgiverRepresentant
import exceptions.UgyldigForespørselException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val ORGNR = "orgnr"
const val VALGTE_PATH = "valgteaktiviteter"
const val FULLFØRTE_PATH = "fullforteaktiviteter"

fun Route.valgteAktiviteter(aktivitetService: AktivitetService) {
    post("/$VALGTE_PATH") {
        val opprettValgtAktivitetDTO = call.receive<OpprettValgtAktivitetDTO>()
        val valgtAktivitet = aktivitetService.velgAktivitet(
            aktivitetsmalId = opprettValgtAktivitetDTO.aktivitetsmalId,
            arbeidsgiverRepresentant = enArbeidsgiverRepresentant
        )
        call.respond(valgtAktivitet.tilDto())
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
    post("/$VALGTE_PATH/{$ORGNR}/$FULLFØRTE_PATH/{$AKTIVITETSMAL_ID}") {
        val aktivitetId = call.parameters[AKTIVITETSMAL_ID] ?: throw UgyldigForespørselException()
        aktivitetService.fullførAktivitet(orgnr = call.orgnr, aktivitetId = aktivitetId.toInt())
        call.respond(status = HttpStatusCode.OK, message = "")
    }
}

val ApplicationCall.virksomhet get() = Virksomhet(this.orgnr)
val ApplicationCall.orgnr get() = this.parameters[ORGNR] ?: throw UgyldigForespørselException()

