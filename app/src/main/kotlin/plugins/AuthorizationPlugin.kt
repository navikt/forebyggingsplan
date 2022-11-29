package plugins

import Miljø
import api.hentVirksomheterSomBrukerHarRiktigRolleI
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
                hentVirksomheterSomBrukerHarRiktigRolleI(
                    token = TokenExchanger.exchangeToken(
                        token = token,
                        audience = Miljø.altinnRettigheterProxyClientId
                    ), subject = fnr
                )

            val orgnr: String = try {
                call.orgnr
            } catch (e: UgyldigForespørselException) {
                call.auditLogVedUkjentOrgnummer(fnr, virksomheterVedkommendeHarTilgangTil)
                throw e
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



