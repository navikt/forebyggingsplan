package api.endepunkt

import api.endepunkt.json.AktivitetJson
import api.endepunkt.json.OppdaterAktivitetJson
import application.AktivitetService
import http.tokenSubject
import http.virksomhet
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import util.hash.Hasher

fun Route.aktiviteter(aktivitetService: AktivitetService) {
    route("/aktiviteter/orgnr/{orgnr}") {
        get("/") {
            val fnr = call.request.tokenSubject()
            val orgnr = call.virksomhet.orgnr
            call.respond(
                aktivitetService.hentAktiviteter(fnr, orgnr).map(AktivitetJson::fraDomene),
            )
        }
    }
}

fun Route.aktivitet(
    aktivitetService: AktivitetService,
    hasher: Hasher,
) {
    route("/aktivitet/{aktivitetId}/orgnr/{orgnr}") {
        post<OppdaterAktivitetJson>("/oppdater") {
            val fødselsnummer = call.request.tokenSubject()
            val aktivitetId = call.parameters["aktivitetId"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Mangler aktivitetId",
            )
            val orgnr = call.virksomhet.orgnr

            val aktivitet = kotlin.runCatching {
                it.tilDomene(hasher.hash(fødselsnummer), orgnr, aktivitetId)
            }.getOrElse {
                return@post call.respond(HttpStatusCode.BadRequest, "Status må være en av ${AktivitetJson.Status.entries.joinToString()}")
            }

            aktivitetService.oppdaterAktivitet(aktivitet)

            call.response.status(HttpStatusCode.OK)
        }
    }
}
