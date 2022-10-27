package api.endepunkt

import api.hentVirksomheterForBruker
import http.hentToken
import http.tokenSubject
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

const val ORGANISASJONER_PATH = "organisasjoner"

fun Route.organisasjoner() {
    get("/$ORGANISASJONER_PATH") {
        val subject = call.request.tokenSubject()
        val token = call.request.hentToken()
        val virksomheter = hentVirksomheterForBruker(token = token, subject = subject).map {
            AltinnVirksomhetDTO(
                name = it.name,
                type = it.type,
                parentOrganizationNumber = it.parentOrganizationNumber,
                organizationNumber = it.organizationNumber,
                organizationForm = it.organizationForm,
                status = it.status,
                socialSecurityNumber = it.socialSecurityNumber
            )
        }
        call.respond(virksomheter)
    }
}

@Serializable
data class AltinnVirksomhetDTO(
    val name: String,
    val type: String,
    val parentOrganizationNumber: String? = null,
    val organizationNumber: String? = null,
    val organizationForm: String? = null,
    val status: String? = null,
    val socialSecurityNumber: String? = null,
)