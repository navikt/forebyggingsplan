package plugins

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
                    .also { call.auditLog(fnr = fnr, tillat = Tillat.Nei) }
            } else {
                call.auditLog(fnr = fnr, orgnummer = orgnr, tillat = Tillat.Ja)
            }
        }
    }
}


private enum class AuditType {
    access, update, create, delete
}

private fun HttpMethod.tilAuditType(): AuditType = when(this){
    HttpMethod.Get -> AuditType.access
    HttpMethod.Post -> AuditType.create
    HttpMethod.Put -> AuditType.update
    HttpMethod.Delete -> AuditType.delete
    else -> AuditType.access
}

enum class Tillat(val tillat: String) {
    Ja("Permit"), Nei("Deny")
}

private fun ApplicationCall.auditLog(
    fnr: String,
    orgnummer: String? = null,
    tillat: Tillat,
): String {
    val auditType = this.request.httpMethod.tilAuditType()
    val method = this.request.httpMethod.value
    val uri = this.request.uri
    val severity = if (orgnummer.isNullOrEmpty()) "WARN" else "INFO"
    val appIdentifikator = "forebyggingsplan"
    return "CEF:0|$appIdentifikator|auditLog|1.0|audit:${auditType.name}|$appIdentifikator|$severity|end=${System.currentTimeMillis()} " +
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
            "flexString1=${tillat.tillat}"
//            (saksnummer?.let { " flexString2Label=saksnummer flexString2=$it" } ?: "")

//    when (miljø) {
//        `PROD-GCP` -> auditLog.info(logstring)
//        Environment.`DEV-GCP` -> Unit
//        Environment.LOKAL -> fiaLog.info(logstring)
//    }
}


