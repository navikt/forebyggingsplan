package plugins

import api.endepunkt.ORGNR
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.AltinnrettigheterProxyKlient
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.AltinnrettigheterProxyKlientConfig
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.ProxyConfig
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.AltinnReportee
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.Subject
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.TokenXToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val AuthorizationPlugin = createRouteScopedPlugin(
    name = "AuthorizationPlugin",
    createConfiguration = ::PluginConfiguration
) {
    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val subject = call.principal<JWTPrincipal>()?.subject
            val bearer = call.request.headers[HttpHeaders.Authorization]
            val token = bearer!!.split(" ")[1]

            val virksomheterVedkommendeHarTilgangTil =
                hentVirksomheterVedkommendeHarTilgangTil(token, subject!!)

            val orgnr = call.parameters[ORGNR]
            println("---------> ORGNR: $orgnr")
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
        filtrerPåAktiveOrganisasjoner = true)

    val log: Logger = LoggerFactory.getLogger("TEMP")
    print("URL: ${Miljø.altinnRettigheterProxyUrl} ")
    log.info("URL: ${Miljø.altinnRettigheterProxyUrl} ")
    println("Organisasjoner???? ${hentOrganisasjoner.size}")
    log.info("Organisasjoner???? ${hentOrganisasjoner.size}")
    return hentOrganisasjoner
}

class PluginConfiguration {

}
