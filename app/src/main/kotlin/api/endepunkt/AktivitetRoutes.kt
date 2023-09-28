package api.endepunkt

import api.dto.AktivitetJson
import api.endepunkt.json.OppdaterAktivitetJson
import application.AktivitetService
import domene.Aktivitet
import http.tokenSubject
import http.virksomhet
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import util.hash.Hasher

fun Route.aktiviteter(aktivitetService: AktivitetService) {
    route("/aktiviteter/orgnr/{orgnr}") {
        get("/") {
            val fnr = call.request.tokenSubject()
            val orgnr = call.virksomhet.orgnr
            call.respond(
                aktivitetService.hentAktiviteter(fnr, orgnr).map(AktivitetJson::fraDomene)
            )
        }
    }
}

fun Route.aktivitet(aktivitetService: AktivitetService, hasher: Hasher) {
    route("/aktivitet/{aktivitetId}/orgnr/{orgnr}") {
        post<OppdaterAktivitetJson>("/oppdater") {
            val fødselsnummer = call.request.tokenSubject()
            val aktivitetId = call.parameters["aktivitetId"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Mangler aktivitetId"
            )
            val orgnr = call.virksomhet.orgnr

            val status = runCatching {
                Aktivitet.Oppgave.Status.valueOf(it.status.uppercase())
            }.getOrElse {
                return@post call.respond(HttpStatusCode.BadRequest, "Status må være en av ${Aktivitet.Oppgave.Status.values().joinToString()}")
            }
            val oppgave = Aktivitet.Oppgave(
                hashetFodselsnummer = hasher.hash(fødselsnummer),
                orgnr = orgnr,
                aktivitetsid = aktivitetId,
                status = status
            )

            aktivitetService.oppdaterOppgave(oppgave)

            call.response.status(HttpStatusCode.OK)
        }
    }
}