package api.endepunkt

import AktivitetService
import api.dto.EndreFristDTO
import api.dto.FullførValgtAktivitetDTO
import api.dto.OpprettValgtAktivitetDTO
import api.sanity.SanityForebyggingsplan
import domene.Aktivitetsmal
import domene.ArbeidsgiverRepresentant
import domene.ValgtAktivitet
import domene.ValgtAktivitet.Companion.velgAktivitet
import domene.Virksomhet
import http.orgnr
import http.virksomhet
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.PipelineContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

const val ORGNR = "orgnr"
const val AKTIVITETS_ID = "aktivitetId"
const val VALGTE_PATH = "valgteaktiviteter"
const val FULLFØR_PATH = "fullfor"
const val ENDRE_FRIST_PATH = "endre-frist"

private val sanityForebyggingsplan = SanityForebyggingsplan("2022-10-28")

fun Route.valgteAktiviteter(aktivitetService: AktivitetService) {
    post("/$VALGTE_PATH/{$ORGNR}") {
        val body = call.receive<OpprettValgtAktivitetDTO>()
        val aktivitetsmalId = body.aktivitetsmalId.toUuidOrNull() ?: return@post call.respond(
            HttpStatusCode.BadRequest, "Aktivitetsmal ${body.aktivitetsmalId} er ikke en UUID"
        )
        val aktivitetsinfo = sanityForebyggingsplan.hentAktivitetsinfo(aktivitetsmalId) ?: return@post call.respond(
            HttpStatusCode.NotFound,
            "Aktivitetsmal $aktivitetsmalId er ukjent"
        )
        if (body.frist != null && body.frist < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
            return@post call.respond(HttpStatusCode.BadRequest, "Frist kan ikke være i fortiden")
        val aktivitet = velgAktivitet(
            aktivitetsmalId = aktivitetsmalId.toString(),
            aktivitetsversjon = aktivitetsinfo.versjon, frist = body.frist
        )
        call.respond(aktivitetService.lagreAktivitet(aktivitet = aktivitet).tilDto())
    }

    post("/$VALGTE_PATH/{$ORGNR}/$ENDRE_FRIST_PATH") {
        val body = call.receive<EndreFristDTO>()
        val virksomhet = call.virksomhet
        aktivitetService.endreFrist(virksomhet, body.aktivitetsId, body.frist)
        return@post call.respond(
            aktivitetService.hentValgtAktivitet(
                virksomhet = virksomhet,
                aktivitetsId = body.aktivitetsId
            ).tilDto()
        )
    }

    get("/$VALGTE_PATH/{$ORGNR}") {
        call.respond(aktivitetService.hentValgteAktiviteterForVirksomhet(call.virksomhet).map(ValgtAktivitet::tilDto))
    }
}

private fun PipelineContext<Unit, ApplicationCall>.velgAktivitet(
    aktivitetsmalId: String,
    aktivitetsversjon: String,
    frist: LocalDate? = null,
    fullført: Boolean = false,
) = ArbeidsgiverRepresentant(virksomhet = Virksomhet(orgnr = call.orgnr))
    .velgAktivitet(
        aktivitetsmal = Aktivitetsmal(id = aktivitetsmalId, versjon = aktivitetsversjon),
        frist = frist,
        fullført = fullført
    )

fun Route.fullførteAktiviteter(aktivitetService: AktivitetService) {
    post("/$FULLFØR_PATH/{$ORGNR}") {
        val body = call.receive<FullførValgtAktivitetDTO>()
        val virksomhet = call.virksomhet
        if (body.aktivitetsId != null) {
            aktivitetService.fullførAktivitet(aktivitetService.hentValgtAktivitet(virksomhet, body.aktivitetsId))
            return@post call.respond(
                aktivitetService.hentValgtAktivitet(
                    virksomhet = virksomhet,
                    aktivitetsId = body.aktivitetsId
                ).tilDto()
            )
        }
        val aktivitetsmalId = body.aktivitetsmalId.toUuidOrNull() ?: return@post call.respond(
            HttpStatusCode.BadRequest, "Aktivitetsmal ${body.aktivitetsmalId} er ikke en UUID"
        )
        val aktivitetsinfo = sanityForebyggingsplan.hentAktivitetsinfo(aktivitetsmalId) ?: return@post call.respond(
            HttpStatusCode.NotFound,
            "Aktivitetsmal $aktivitetsmalId er ukjent"
        )
        val aktivitet = aktivitetService.lagreAktivitet(
            velgAktivitet(
                aktivitetsmalId = aktivitetsmalId.toString(),
                aktivitetsversjon = aktivitetsinfo.versjon,
                fullført = true
            )
        )
        return@post call.respond(
            aktivitetService.hentValgtAktivitet(
                virksomhet = virksomhet,
                aktivitetsId = aktivitet.id
            ).tilDto()
        )
    }
}

private fun String.toUuidOrNull(): UUID? {
    return kotlin.runCatching { UUID.fromString(this) }.getOrNull()
}
