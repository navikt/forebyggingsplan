package api.endepunkt

import api.dto.AktivitetJson
import api.dto.FullførtAktivitetJson
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

fun Route.fullførteAktiviteter(aktivitetService: AktivitetService) {
    route("/aktivitet/{aktivitetsid}/versjon/{aktivitetsversjon}/orgnr/{orgnr}") {
        post("/fullfor") {
            val fødselsnummer = call.request.tokenSubject()
            val aktivitetsId = call.parameters["aktivitetsid"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler aktivitetsid")

            // AuthorizationPlugin påser at brukeren representerer innsendt orgnr
            val orgnr = call.parameters["orgnr"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler orgnr")

            aktivitetService.fullførAktivitet(
                fødselsnummer = fødselsnummer,
                aktivitetsid = aktivitetsId,
                orgnr = orgnr,
            )

            call.respond(HttpStatusCode.OK)
        }
    }
    route("/aktiviteter/orgnr/{orgnr}") {
        get("/") {
            val fnr = call.request.tokenSubject()
            val orgnr = call.virksomhet.orgnr
            call.respond(
                aktivitetService.hentAktiviteter(fnr, orgnr).map(AktivitetJson::fraDomene)
            )
        }

        get("/fullforte") {
            val fnr = call.request.tokenSubject()
            val virksomhet = call.virksomhet
            call.respond(
                aktivitetService.hentAlleFullførteAktiviteterFor(fnr, virksomhet)
                    .map(FullførtAktivitetJson::fraDomene)
            )
        }
    }
}

fun Route.aktiviteter(aktivitetService: AktivitetService, hasher: Hasher) {
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