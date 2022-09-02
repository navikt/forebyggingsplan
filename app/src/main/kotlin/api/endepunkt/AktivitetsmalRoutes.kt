package api.endepunkt

import AktivitetService
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.Payload
import domene.Aktivitetsmal
import io.ktor.client.engine.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.AltinnrettigheterProxyKlient
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.AltinnrettigheterProxyKlientConfig
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.ProxyConfig
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.AltinnReportee
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.Subject
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.TokenXToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val AKTIVITETSMALER_PATH = "aktivitetsmaler"
const val AKTIVITETSMAL_ID ="aktivitetsmalId"

fun Route.aktivitetsmaler(aktivitetService: AktivitetService) {
    get("/$AKTIVITETSMALER_PATH") {
        val bearer = call.request.headers[HttpHeaders.Authorization]
        val token = bearer!!.split(" ")[1]
        val decodedJWT = JWT.decode(token)
        val virksomheterVedkommendeHarTilgangTil =
            hentVirksomheterVedkommendeHarTilgangTil(token, decodedJWT.subject)

        if (virksomheterVedkommendeHarTilgangTil.isEmpty()) {
            call.response.apply {
                status(HttpStatusCode.Forbidden)
            }
            return@get
        }

        call.respond(aktivitetService.hentAktivitetsmaler().map(Aktivitetsmal::tilDto))
    }
}

fun hentVirksomheterVedkommendeHarTilgangTil(token: String, subject: String): List<AltinnReportee> {
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


