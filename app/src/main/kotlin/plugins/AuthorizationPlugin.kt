package plugins

import Miljø
import api.dto.OpprettValgtAktivitetDTO
import api.endepunkt.ORGNR
import auth.TokenExchanger
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.AltinnrettigheterProxyKlient
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.AltinnrettigheterProxyKlientConfig
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.ProxyConfig
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.AltinnReportee
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.ServiceCode
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.ServiceEdition
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.Subject
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.TokenXToken

val SYKEFRAVÆRSSTATISTIKK_RETTIGHETER = AltinnRettighetKoder(
    serviceCode = "3403",
    serviceEdition = Miljø.altinnRettighetServiceEdition
)

val AuthorizationPlugin = createRouteScopedPlugin(
    name = "AuthorizationPlugin",
) {
    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val subject = call.principal<JWTPrincipal>()?.subject ?: throw RuntimeException("Subject missing in JWT")
            val bearer = call.request.headers[HttpHeaders.Authorization]
                ?: throw RuntimeException("No Authorization header found")
            val token = removeBearerPrefix(bearer)

            val virksomheterVedkommendeHarTilgangTil =
                hentVirksomheterVedkommendeHarTilgangTil(
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

private fun removeBearerPrefix(bearer: String) = bearer.split(" ")[1]

private fun hentVirksomheterVedkommendeHarTilgangTil(token: String, subject: String): List<AltinnReportee> {
    return AltinnrettigheterProxyKlient(
        AltinnrettigheterProxyKlientConfig(
            ProxyConfig(
                consumerId = "Forebyggingsplan",
                url = Miljø.altinnRettigheterProxyUrl
            )
        )
    ).hentOrganisasjoner(
        selvbetjeningToken = TokenXToken(value = token),
        subject = Subject(subject),
        serviceCode = ServiceCode(SYKEFRAVÆRSSTATISTIKK_RETTIGHETER.serviceCode),
        serviceEdition = ServiceEdition(SYKEFRAVÆRSSTATISTIKK_RETTIGHETER.serviceEdition),
        filtrerPåAktiveOrganisasjoner = true
    )
}

class AltinnRettighetKoder(
    val serviceCode: String,
    val serviceEdition: String
)

