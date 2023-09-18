package api.endepunkt

import api.dto.FullførtAktivitetJson
import application.AktivitetService
import http.tokenSubject
import http.virksomhet
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.fullførteAktiviteter(aktivitetService: AktivitetService) {
    route("/aktivitet/{aktivitetsid}/versjon/{aktivitetsversjon}/orgnr/{orgnr}") {
        post("/fullfor") {
            val fødselsnummer = call.request.tokenSubject()
            val aktivitetsId = call.parameters["aktivitetsid"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler aktivitetsid")
            val aktivitetsVersjon = call.parameters["aktivitetsversjon"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler aktivitetsversjon")

            // AuthorizationPlugin påser at brukeren representerer innsendt orgnr
            val orgnr = call.parameters["orgnr"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler orgnr")

            aktivitetService.fullførAktivitet(
                fødselsnummer = fødselsnummer,
                aktivitetsid = aktivitetsId,
                orgnr = orgnr,
                aktivitetsversjon = aktivitetsVersjon
            )

            call.respond(HttpStatusCode.OK)
        }
    }
    route("/aktiviteter/orgnr/{orgnr}") {
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