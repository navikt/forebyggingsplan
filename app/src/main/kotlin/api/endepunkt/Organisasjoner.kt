package api.endepunkt

import api.hentVirksomheterForBruker
import auth.TokenExchanger
import http.hentToken
import http.tokenSubject
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val ORGANISASJONER_PATH = "organisasjoner"

fun Route.organisasjoner() {
    get("/$ORGANISASJONER_PATH") {
        val subject = call.request.tokenSubject()
        val token = call.request.hentToken()
        val virksomheter = hentVirksomheterForBruker(
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