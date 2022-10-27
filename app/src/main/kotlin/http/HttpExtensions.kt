package http

import exceptions.UgyldigForespørselException
import io.ktor.http.HttpHeaders
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.ApplicationRequest

fun removeBearerPrefix(bearer: String) = bearer.split(" ")[1]

fun ApplicationRequest.hentToken() = removeBearerPrefix(this.headers[HttpHeaders.Authorization] ?: throw UgyldigForespørselException("No Authorization header found"))
fun ApplicationRequest.tokenSubject() = call.principal<JWTPrincipal>()?.subject ?: throw UgyldigForespørselException("Subject missing in JWT")