package api.endepunkt

import api.hentVirksomheterForBruker
import http.hentToken
import http.tokenSubject
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

const val ORGANISASJONER_PATH = "organisasjoner"

fun Route.organisasjoner() {
    get("/$ORGANISASJONER_PATH") {
        val subject = call.request.tokenSubject()
        val token = call.request.hentToken()
        val virksomheter = hentVirksomheterForBruker(token = token, subject = subject)
        call.respond(virksomheter)
    }
}