package api.endepunkt

import AktivitetService
import api.dto.OpprettValgtAktivitetDTO
import domene.ValgtAktivitet
import domene.FullførtAktivitet
import domene.Virksomhet
import domene.enArbeidsgiverRepresentant
import exceptions.UgyldigForespørselException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val ORGNR ="orgnr"
const val VALGTE_PATH = "valgteaktiviteter"
const val FULLFØRTE_PATH = "fullførteaktiviteter"

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
    post("/$AKTIVITETSMALER_PATH/{$ORGNR}/$FULLFØRTE_PATH/{$AKTIVITETSMAL_ID}") {
        call.respond(status = HttpStatusCode.NotImplemented, message = "")
    }

    get("/$AKTIVITETSMALER_PATH/{$ORGNR}/$FULLFØRTE_PATH") {
        call.respond(aktivitetService.hentFullførteAktiviteterForVirksomhet(call.virksomhet).map(FullførtAktivitet::tilDto))
    }
}

val ApplicationCall.virksomhet get() = Virksomhet(this.orgnr)
val ApplicationCall.orgnr get() = this.parameters[ORGNR] ?: throw UgyldigForespørselException()
val ApplicationCall.aktivitetsmalId get () = this.parameters[AKTIVITETSMAL_ID] ?: throw UgyldigForespørselException()

