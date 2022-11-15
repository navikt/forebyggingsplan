package api.endepunkt

import AktivitetService
import api.dto.FullførValgtAktivitetDTO
import api.dto.OpprettValgtAktivitetDTO
import domene.Aktivitetsmal
import domene.ArbeidsgiverRepresentant
import domene.ValgtAktivitet
import domene.ValgtAktivitet.Companion.velgAktivitet
import domene.Virksomhet
import http.aktivitetsId
import http.orgnr
import http.virksomhet
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.PipelineContext
import kotlinx.datetime.LocalDate

const val ORGNR = "orgnr"
const val AKTIVITETS_ID = "aktivitetId"
const val VALGTE_PATH = "valgteaktiviteter"
const val FULLFØR_PATH = "fullfor"

fun Route.valgteAktiviteter(aktivitetService: AktivitetService) {
    post("/$VALGTE_PATH/{$ORGNR}") {
        val body = call.receive<OpprettValgtAktivitetDTO>()
        val aktivitet = velgAktivitet(body.aktivitetsmalId, frist = body.frist)
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

private fun PipelineContext<Unit, ApplicationCall>.velgAktivitet(
    aktivitetsmalId: String,
    frist: LocalDate? = null,
    fullført: Boolean = false,
) = ArbeidsgiverRepresentant(fnr = "", virksomhet = Virksomhet(orgnr = call.orgnr))
    .velgAktivitet(aktivitetsmal = Aktivitetsmal(id = aktivitetsmalId), frist = frist, fullført = fullført)


fun Route.fullførteAktiviteter(aktivitetService: AktivitetService) {
    post("/$FULLFØR_PATH/{$ORGNR}") {
        val body = call.receive<FullførValgtAktivitetDTO>()
        val virksomhet = call.virksomhet
        if (body.aktivitetsId != null) {
            aktivitetService.fullførAktivitet(aktivitetService.hentValgtAktivitet(virksomhet, body.aktivitetsId))
            return@post call.respond(
                aktivitetService.hentValgtAktivitet(
                    virksomhet = virksomhet,
                    aktivitetsId = body.aktivitetsId).tilDto()
            )
        }
        val aktivitet = aktivitetService.lagreAktivitet(velgAktivitet(body.aktivitetsmalId, fullført = true))
        return@post call.respond(
            aktivitetService.hentValgtAktivitet(
                virksomhet = virksomhet,
                aktivitetsId = aktivitet.id
            ).tilDto()
        )
    }
}

