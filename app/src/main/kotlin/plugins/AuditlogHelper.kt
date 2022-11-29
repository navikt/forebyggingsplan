package plugins

import Clusters.DEV_GCP
import Clusters.LOKAL
import Clusters.PROD_GCP
import io.ktor.http.HttpMethod
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.receiveText
import io.ktor.server.request.uri
import io.ktor.util.toMap
import no.nav.arbeidsgiver.altinnrettigheter.proxy.klient.model.AltinnReportee
import org.slf4j.LoggerFactory
import java.util.UUID


enum class AuditType {
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

fun ApplicationCall.auditLogVedUkjentOrgnummer(
    fnr: String,
    virksomheter: List<AltinnReportee>,
) {
    this.auditLog(
        fnr = fnr,
        tillat = Tillat.Nei,
        beskrivelse = "finner ikke organisjasjonsnummeret i requesten fra bruker $fnr",
        virksomheter = virksomheter,
    )
}

fun ApplicationCall.auditLogVedIkkeTilgangTilOrg(
    fnr: String,
    orgnr: String,
    virksomheter: List<AltinnReportee>,
) {
    this.auditLog(
        fnr = fnr,
        orgnummer = orgnr,
        tillat = Tillat.Nei,
        beskrivelse = "$fnr har ikke tilgang til organisasjonsnummer $orgnr",
        virksomheter = virksomheter
    )
}

suspend fun ApplicationCall.auditLogVedOkKall(
    fnr: String,
    orgnummer: String,
    virksomheter: List<AltinnReportee>,
) {
    this.auditLog(
        fnr = fnr,
        orgnummer = orgnummer,
        tillat = Tillat.Ja,
        beskrivelse = "$fnr har gjort følgende mot organisajonsnummer $orgnummer " +
                "path: ${this.request.path()} " +
                "arg: ${this.request.queryParameters.toMap()} " +
                "body: ${this.receiveText()}",
        virksomheter = virksomheter
    )
}

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
    val logstring =
        "CEF:0|$appIdentifikator|auditLog|1.0|audit:${auditType.name}|Sporingslogg|$severity|end=${System.currentTimeMillis()} " +
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
                "flexString2Label=VirksomheterSomBrukerRepresenterer " +
                "flexString2=${virksomheterSomBrukerRepresenterer} " +
                "msg=${beskrivelse} "

    when (Miljø.cluster) {
        PROD_GCP.clusterId -> auditLog.info(logstring)
        DEV_GCP.clusterId -> auditLog.info(logstring)
        LOKAL.clusterId -> fiaLog.info(logstring)
    }
}
