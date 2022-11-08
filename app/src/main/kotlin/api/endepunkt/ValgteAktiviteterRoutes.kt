package api.endepunkt

import AktivitetService
import api.dto.FullførValgtAktivitetDTO
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
import kotlinx.serialization.json.Json

const val ORGNR = "orgnr"
const val AKTIVITETS_ID = "aktivitetId"
const val VALGTE_PATH = "valgteaktiviteter"
const val FULLFØR_PATH = "fullfor"

fun Route.valgteAktiviteter(aktivitetService: AktivitetService) {
    post("/$VALGTE_PATH/{$ORGNR}") {
        val body = call.receive<OpprettValgtAktivitetDTO>()
        val aktivitet = ArbeidsgiverRepresentant(fnr = "", virksomhet = Virksomhet(orgnr = call.orgnr))
            .velgAktivitet(aktivitetsmal = Aktivitetsmal(id = body.aktivitetsmalId))
        call.respond(aktivitetService.lagreAktivitet(aktivitet = aktivitet).tilDto())
    }

    get("/$VALGTE_PATH/{$ORGNR}") {
        call.respond(aktivitetService.hentValgteAktiviteterForVirksomhet(call.virksomhet).map(ValgtAktivitet::tilDto))
    }

    get("/$VALGTE_PATH/{$ORGNR}/{$AKTIVITETS_ID}") {
        call.respond(
            aktivitetService.hentValgtAktivitet(
                virksomhet = call.virksomhet,
                aktivitetsId = call.aktivitetsId
            ).tilDto()
        )
    }
}


fun Route.fullførteAktiviteter(aktivitetService: AktivitetService) {
    post("/$FULLFØR_PATH/{$ORGNR}") {
        val body = call.receive<FullførValgtAktivitetDTO>()
        val virksomhet = call.virksomhet
        aktivitetService.fullførAktivitet(aktivitetService.hentValgtAktivitet(virksomhet, body.aktivitetsId))
        val hentValgtAktivitet = aktivitetService.hentValgtAktivitet(
            virksomhet = virksomhet,
            aktivitetsId = body.aktivitetsId
        )
        call.respond(
            hentValgtAktivitet.tilDto()
        )
    }
}

val ApplicationCall.virksomhet get() = Virksomhet(this.orgnr)
val ApplicationCall.orgnr
    get() = this.parameters[ORGNR] ?: throw UgyldigForespørselException("Manglende parameter 'orgnr'")
val ApplicationCall.aktivitetsId
    get() = this.parameters[AKTIVITETS_ID]?.toInt()
        ?: throw UgyldigForespørselException("Manglende parameter 'aktivitetsId'")

