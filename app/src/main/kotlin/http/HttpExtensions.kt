package http

import domene.Virksomhet
import exceptions.UgyldigForespørselException
import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.ApplicationRequest

fun removeBearerPrefix(bearer: String) = bearer.split(" ")[1]

fun ApplicationRequest.hentToken() =
    removeBearerPrefix(
        this.headers[HttpHeaders.Authorization] ?: throw UgyldigForespørselException("No Authorization header found"),
    )

fun ApplicationRequest.tokenSubject() =
    call.principal<JWTPrincipal>()?.get("pid") ?: throw UgyldigForespørselException("pid missing in JWT")

val ApplicationCall.virksomhet get() = Virksomhet(this.orgnr)

val ApplicationCall.orgnr
    get() = this.parameters["orgnr"] ?: throw UgyldigForespørselException("Manglende parameter 'orgnr'")
