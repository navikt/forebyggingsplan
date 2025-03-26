package plugins

import application.AltinnTilgangerService
import application.AltinnTilgangerService.Companion.harTilgangTilOrgnr
import application.AltinnTilgangerService.Companion.virksomheterVedkommendeHarTilgangTil
import exceptions.UgyldigForespørselException
import http.hentToken
import http.orgnr
import http.tokenSubject
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.authentication
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("ktlint:standard:function-naming")
fun AltinnAuthorizationPlugin(altinnTilgangerService: AltinnTilgangerService) =
    createRouteScopedPlugin(name = "AuthorizationPlugin") {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
        pluginConfig.apply {
            on(AuthenticationChecked) { call ->

                if (call.authentication.allErrors.isNotEmpty()) {
                    logger.warn("Authentication errors: ${call.authentication.allErrors}")
                    call.respond(
                        status = HttpStatusCode.Unauthorized,
                        ResponseIError(message = "Unauthorized"),
                    )
                    return@on
                }
                if (call.authentication.allFailures.isNotEmpty()) {
                    call.respond(
                        status = HttpStatusCode.Unauthorized,
                        ResponseIError(message = "Unauthorized"),
                    )
                    return@on
                }

                val fnr = call.request.tokenSubject()
                val token = call.request.hentToken()

                val altinnTilganger = altinnTilgangerService.hentAltinnTilganger(token = token)

                val orgnr: String? = kotlin.runCatching { call.orgnr }.getOrNull()
                if (orgnr.isNullOrEmpty()) {
                    call.auditLogVedUkjentOrgnummer(fnr)
                    throw UgyldigForespørselException("Manglende parameter 'orgnr'")
                }

                if (!orgnr.erEtOrgNummer()) {
                    call.auditLogVedUgyldigOrgnummer(fnr, orgnr)
                    throw UgyldigForespørselException("Ugyldig orgnummer 'orgnr'")
                }

                if (!altinnTilganger.harTilgangTilOrgnr(orgnr)) {
                    call.respond(
                        status = HttpStatusCode.Forbidden,
                        message = ResponseIError(message = "Bruker har ikke tilgang til virksomheten"),
                    ).also {
                        call.auditLogVedIkkeTilgangTilOrg(
                            fnr = fnr,
                            orgnr = orgnr,
                            virksomheter = altinnTilganger.virksomheterVedkommendeHarTilgangTil(),
                        )
                    }
                } else {
                    call.auditLogVedOkKall(
                        fnr = fnr,
                        orgnummer = orgnr,
                        virksomheter = altinnTilganger.virksomheterVedkommendeHarTilgangTil(),
                    )
                }
            }
        }
    }

private fun String.erEtOrgNummer() = this.matches("^[0-9]{9}$".toRegex())

@Serializable
data class ResponseIError(
    val message: String,
)
