package api.endepunkt

import api.endepunkt.json.AktivitetJson
import api.endepunkt.json.OppdaterAktivitetJson
import application.AktivitetService
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

            val aktivitet = kotlin.runCatching {
                it.tilDomene(hasher.hash(fødselsnummer), orgnr, aktivitetId)
            }.getOrElse {
                return@post call.respond(HttpStatusCode.BadRequest, "Status må være en av ${AktivitetJson.Status.values().joinToString()}")
            }

            aktivitetService.oppdaterAktivitet(aktivitet)

            call.response.status(HttpStatusCode.OK)
        }
    }
}