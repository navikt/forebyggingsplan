package api.endepunkt

import Miljø
import api.endepunkt.Logger.logger
import api.hentVirksomheterSomBrukerRepresenterer
import auth.TokenExchanger
import http.hentToken
import http.tokenSubject
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val ORGANISASJONER_PATH = "organisasjoner"
object Logger {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)
}

fun Route.organisasjoner() {
    get("/$ORGANISASJONER_PATH") {
        logger.info("[Organisajoner] GET ")
        val subject = call.request.tokenSubject()
        val token = call.request.hentToken()
        val virksomheter = hentVirksomheterSomBrukerRepresenterer(
            token = TokenExchanger.exchangeToken(
                token = token,
                audience = Miljø.altinnRettigheterProxyClientId
            ), subject = subject
        ).map {
            AltinnVirksomhetDTO(
                navn = it.name,
                type = it.type,
                juridiskEnhet = it.parentOrganizationNumber,
                orgnummer = it.organizationNumber,
                organisasjonsform = it.organizationForm,
                status = it.status,
                fødselsnummer = it.socialSecurityNumber
            )
        }
        logger.info("[Organisajoner] hentet ${virksomheter.size} virksomheter")
        call.respond(virksomheter)
    }
}

@Serializable
data class AltinnVirksomhetDTO(
    @SerialName("Name")
    val navn: String,
    @SerialName("Type")
    val type: String,
    @SerialName("ParentOrganizationNumber")
    val juridiskEnhet: String? = null,
    @SerialName("OrganizationNumber")
    val orgnummer: String? = null,
    @SerialName("OrganizationForm")
    val organisasjonsform: String? = null,
    @SerialName("Status")
    val status: String? = null,
    @SerialName("SocialSecurityNumber")
    val fødselsnummer: String? = null,
)
