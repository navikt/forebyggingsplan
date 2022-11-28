package plugins

import Clusters.DEV_GCP
import Clusters.LOKAL
import Clusters.PROD_GCP
import Miljø
import api.hentVirksomheterSomBrukerHarRiktigRolleI
import auth.TokenExchanger
import http.hentToken
import http.orgnr
import http.tokenSubject
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.util.toMap
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.AltinnReportee
import org.slf4j.LoggerFactory
import java.util.UUID

val AuthorizationPlugin = createRouteScopedPlugin(
    name = "AuthorizationPlugin",
) {
    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val fnr = call.request.tokenSubject()
            val token = call.request.hentToken()
            val orgnr = call.orgnr

            val virksomheterVedkommendeHarTilgangTil =
                hentVirksomheterSomBrukerHarRiktigRolleI(
                    token = TokenExchanger.exchangeToken(
                        token = token,
                        audience = Miljø.altinnRettigheterProxyClientId
                    ), subject = fnr
                )

            if (virksomheterVedkommendeHarTilgangTil.none { it.organizationNumber == orgnr }) {
                call.respond(status = HttpStatusCode.Forbidden, "Bruker har ikke tilgang til virksomheten")
                    .also {
                        call.auditLog(
                            fnr = fnr,
                            tillat = Tillat.Nei,
                            beskrivelse = "$fnr har ikke tilgang til organisasjonsnummer $orgnr",
                            virksomheter = virksomheterVedkommendeHarTilgangTil
                        )
                    }
            } else {
                call.auditLog(
                    fnr = fnr,
                    orgnummer = orgnr,
                    tillat = Tillat.Ja,
                    beskrivelse = "$fnr har gjort følgende mot organisajonsnummer $orgnr " +
                            "path: ${call.request.path()} " +
                            "arg: ${call.request.queryParameters.toMap()} " +
                            "body: ${call.request.pipeline.attributes.allKeys}",
                    virksomheter = virksomheterVedkommendeHarTilgangTil
                )
            }
        }
    }
}


private enum class AuditType {
    access, update, create, delete
}

private fun HttpMethod.tilAuditType(): AuditType = when (this) {
    HttpMethod.Get -> AuditType.access
    HttpMethod.Post -> AuditType.create
    HttpMethod.Put -> AuditType.update
    HttpMethod.Delete -> AuditType.delete
    else -> AuditType.access
}

enum class Tillat(val tillat: String) {
    Ja("Permit"), Nei("Deny")
}

private val auditLog = LoggerFactory.getLogger("auditLogger")
private val fiaLog = LoggerFactory.getLogger("auditLogLokal")

private fun ApplicationCall.auditLog(
    fnr: String,
    orgnummer: String? = null,
    tillat: Tillat,
    beskrivelse: String,
    virksomheter: List<AltinnReportee>,
) {
    val auditType = this.request.httpMethod.tilAuditType()
    val method = this.request.httpMethod.value
    val uri = this.request.uri
    val severity = if (orgnummer.isNullOrEmpty()) "WARN" else "INFO"
    val appIdentifikator = "forebyggingsplan"
    val virksomheterSomBrukerRepresenterer = virksomheter.map { it.organizationNumber }.joinToString()
    val logstring = "CEF:0|$appIdentifikator|auditLog|1.0|audit:${auditType.name}|$appIdentifikator|$severity|end=${System.currentTimeMillis()} " +
            "suid=$fnr " +
            (orgnummer?.let { "duid=$it " } ?: "") +
            "sproc=${UUID.randomUUID()} " +
            "requestMethod=$method " +
            "request=${
                uri.substring(
                    0,
                    uri.length.coerceAtMost(70)
                )
            } " +
            "flexString1Label=Decision " +
            "flexString1=${tillat.tillat} " +
            "flexString2Label=Beskrivelse " +
            "flexString2=${beskrivelse} " +
            "flexString3Label=VirksomheterSomBrukerRepresenterer " +
            "flexString3=${virksomheterSomBrukerRepresenterer} "

    when (Miljø.cluster) {
        PROD_GCP.clusterId -> auditLog.info(logstring)
        DEV_GCP.clusterId -> auditLog.info(logstring)
        LOKAL.clusterId -> fiaLog.info(logstring)
    }
}


