package plugins

import Miljø
import api.dto.OpprettValgtAktivitetDTO
import api.endepunkt.ORGNR
import api.hentVirksomheterForBruker
import auth.TokenExchanger
import http.hentToken
import http.tokenSubject
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.request.receive
import io.ktor.server.response.respond

val AuthorizationPlugin = createRouteScopedPlugin(
    name = "AuthorizationPlugin",
) {
    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val subject = call.request.tokenSubject()
            val token = call.request.hentToken()

            val virksomheterVedkommendeHarTilgangTil =
                hentVirksomheterForBruker(
                    token = TokenExchanger.exchangeToken(
                        token = token,
                        audience = Miljø.altinnRettigheterProxyClientId
                    ), subject = subject
                )

            val orgnr =
                if (call.parameters[ORGNR] != null)
                    call.parameters[ORGNR]
                else
                    call.receive<OpprettValgtAktivitetDTO>().orgnr

            if (virksomheterVedkommendeHarTilgangTil.none { it.organizationNumber == orgnr }) {
                call.respond(status = HttpStatusCode.Forbidden, "")
            }
        }
    }
}

