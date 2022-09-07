package plugins

import Miljø
import api.dto.OpprettValgtAktivitetDTO
import api.endepunkt.ORGNR
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
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
    serviceEdition = "1"
)

val AuthorizationPlugin = createRouteScopedPlugin(
    name = "AuthorizationPlugin",
) {

    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val subject = call.principal<JWTPrincipal>()?.subject
            val bearer = call.request.headers[HttpHeaders.Authorization]
            val token = bearer!!.split(" ")[1]

            val virksomheterVedkommendeHarTilgangTil =
                hentVirksomheterVedkommendeHarTilgangTil(token, subject!!)

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

private fun hentVirksomheterVedkommendeHarTilgangTil(token: String, subject: String): List<AltinnReportee> {
    val hentOrganisasjoner = AltinnrettigheterProxyKlient(
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
        filtrerPåAktiveOrganisasjoner = true)

    return hentOrganisasjoner
}

class AltinnRettighetKoder(
    val serviceCode: String,
    val serviceEdition: String
)
