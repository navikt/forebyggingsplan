package api.endepunkt

import AktivitetService
import domene.*
import exceptions.UgyldigForespørselException
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
        val aktivitetsId = call.aktivitetsId
        val valgtAktivitet = aktivitetService.velgAktivitet(
            aktivitetsId = aktivitetsId,
            arbeidsgiverRepresentant = enArbeidsgiverRepresentant
        )
        call.respond(valgtAktivitet.tilDto())
    }

    get("/$AKTIVITETER_PATH/{${parameters.orgnr}}/$VALGTE_PATH") {
        call.respond(aktivitetService.hentValgteAktiviteterForVirksomhet(call.virksomhet).map(ValgtAktivitet::tilDto))
    }

    post("/$AKTIVITETER_PATH/{${parameters.orgnr}}/$FULLFØRTE_PATH/{${parameters.aktivitetsId}}") {
        call.respond(status = HttpStatusCode.NotImplemented, message = "")
    }

    get("/$AKTIVITETER_PATH/{${parameters.orgnr}}/$FULLFØRTE_PATH") {
        call.respond(aktivitetService.hentFullførteAktiviteterForVirksomhet(call.virksomhet).map(FullførtAktivitet::tilDto))
    }
}
val ApplicationCall.virksomhet get() = Virksomhet(this.orgnr)
val ApplicationCall.orgnr get() = this.parameters["orgnummer"] ?: throw UgyldigForespørselException()
val ApplicationCall.aktivitetsId get () = this.parameters["aktivitetsId"] ?: throw UgyldigForespørselException()

