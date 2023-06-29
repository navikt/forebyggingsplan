package plugins

import Systemmiljø
import api.endepunkt.ORGNR
import api.hentVirksomheterSomBrukerHarRiktigEnkelRettighetI
import auth.TokenExchanger
import exceptions.UgyldigForespørselException
import http.hentToken
import http.orgnr
import http.tokenSubject
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.response.respond

val AuthorizationPlugin = createRouteScopedPlugin(
    name = "AuthorizationPlugin",
) {
    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val fnr = call.request.tokenSubject()
            val token = call.request.hentToken()

            val virksomheterVedkommendeHarTilgangTil =
                hentVirksomheterSomBrukerHarRiktigEnkelRettighetI(
                    token = TokenExchanger.exchangeToken(
                        token = token,
                        audience = Systemmiljø.altinnRettigheterProxyClientId
                    ), subject = fnr
                )

            val orgnr = kotlin.runCatching { call.orgnr }.getOrNull()
            if (orgnr.isNullOrEmpty()) {
                call.auditLogVedUkjentOrgnummer(fnr, virksomheterVedkommendeHarTilgangTil)
                throw UgyldigForespørselException("Manglende parameter '$ORGNR'")
            }

            if (!orgnr.erEtOrgNummer()) {
                call.auditLogVedUgyldigOrgnummer(fnr, orgnr, virksomheterVedkommendeHarTilgangTil)
                throw UgyldigForespørselException("Ugyldig orgnummer '$ORGNR'")
            }

            if (virksomheterVedkommendeHarTilgangTil.none { it.organizationNumber == orgnr }) {
                call.respond(status = HttpStatusCode.Forbidden, "Bruker har ikke tilgang til virksomheten")
                    .also { call.auditLogVedIkkeTilgangTilOrg(fnr, orgnr, virksomheterVedkommendeHarTilgangTil) }
            } else {
                call.auditLogVedOkKall(fnr, orgnr, virksomheterVedkommendeHarTilgangTil)
            }
        }
    }
}

private fun String.erEtOrgNummer() = this.matches("^[0-9]{9}$".toRegex())



