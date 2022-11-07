package api.endepunkt

import AktivitetService
import api.dto.OpprettValgtAktivitetDTO
import domene.Aktivitetsmal
import domene.ArbeidsgiverRepresentant
import domene.ValgtAktivitet
import domene.ValgtAktivitet.Companion.velgAktivitet
import domene.Virksomhet
import exceptions.UgyldigForespørselException
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val ORGNR = "orgnr"
const val AKTIVITETS_ID = "aktivitetId"
const val VALGTE_PATH = "valgteaktiviteter"
const val FULLFØRTE_PATH = "fullforteaktiviteter"

fun Route.valgteAktiviteter(aktivitetService: AktivitetService) {
    post("/$VALGTE_PATH") {
        val body = call.receive<OpprettValgtAktivitetDTO>()
        val aktivitet = ArbeidsgiverRepresentant(fnr = "", virksomhet = Virksomhet(orgnr = body.orgnr))
            .velgAktivitet(aktivitetsmal = Aktivitetsmal(id = body.aktivitetsmalId))
        call.respond(aktivitetService.lagreAktivitet(aktivitet = aktivitet).tilDto())
    }

    get("/$VALGTE_PATH/{$ORGNR}") {
        call.respond(aktivitetService.hentValgteAktiviteterForVirksomhet(call.virksomhet).map(ValgtAktivitet::tilDto))
    }

    get("/$VALGTE_PATH/{$ORGNR}/{$AKTIVITETS_ID}") {
        call.respond(aktivitetService.hentValgtAktivitet(
            virksomhet = call.virksomhet,
            aktivitetsId = call.aktivitetsId).tilDto()
        )
    }
}


fun Route.fullførteAktiviteter(aktivitetService: AktivitetService) {
    post("/$VALGTE_PATH/{$ORGNR}/$FULLFØRTE_PATH/{$AKTIVITETS_ID}") {
        aktivitetService.fullførAktivitet(aktivitetService.hentValgtAktivitet(call.virksomhet, call.aktivitetsId))
        call.respond(aktivitetService.hentValgtAktivitet(
            virksomhet = call.virksomhet,
            aktivitetsId = call.aktivitetsId).tilDto()
        )
    }
}

val ApplicationCall.virksomhet get() = Virksomhet(this.orgnr)
val ApplicationCall.orgnr get() = this.parameters[ORGNR] ?: throw UgyldigForespørselException("Manglende parameter 'orgnr'")
val ApplicationCall.aktivitetsId get() = this.parameters[AKTIVITETS_ID]?.toInt() ?: throw UgyldigForespørselException("Manglende parameter 'aktivitetsId'")

