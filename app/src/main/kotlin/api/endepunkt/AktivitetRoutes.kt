package api.endepunkt

import application.AktivitetService
import http.tokenSubject
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.fullførAktivitet(aktivitetService: AktivitetService) {
    route("/aktivitet/{aktivitetsid}/versjon/{aktivitetsversjon}/orgnr/{orgnr}") {
        post("/fullfor") {
            val fødselsnummer = call.request.tokenSubject()
            val aktivitetsid = call.parameters["aktivitetsid"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler aktivitetsid")
            val aktivitetsversjon = call.parameters["aktivitetsversjon"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler aktivitetsversjon")

            // AuthorizationPlugin påser at brukeren representerer innsendt orgnr
            val orgnr = call.parameters["orgnr"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler orgnr")

            aktivitetService.fullførAktivitet(fødselsnummer, aktivitetsid, aktivitetsversjon, orgnr)

            call.respond(HttpStatusCode.OK)
        }
    }
}