package plugins

import Miljø
import api.endepunkt.ORGNR
import api.hentVirksomheterSomBrukerHarRiktigRolleI
import auth.TokenExchanger
import http.hentToken
import http.tokenSubject
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.response.respond
import java.util.UUID

val AuthorizationPlugin = createRouteScopedPlugin(
    name = "AuthorizationPlugin",
) {
    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val subject = call.request.tokenSubject()
            val token = call.request.hentToken()

            val virksomheterVedkommendeHarTilgangTil =
                hentVirksomheterSomBrukerHarRiktigRolleI(
                    token = TokenExchanger.exchangeToken(
                        token = token,
                        audience = Miljø.altinnRettigheterProxyClientId
                    ), subject = subject
                )

            val orgnr =
                call.parameters[ORGNR] ?: call.respond(status = HttpStatusCode.Forbidden, "Ukjent organisajonsnummer!")

            if (virksomheterVedkommendeHarTilgangTil.none { it.organizationNumber == orgnr }) {
                call.respond(status = HttpStatusCode.Forbidden, "")
            }
        }
    }
}

enum class AuditType {
    access, update, create
}

enum class Tillat(val tillat: String) {
    Ja("Permit"), Nei("Deny")
}

private fun auditLog(
    auditType: AuditType,
    fnr: String,
    orgnummer: String?,
    method: String,
    uri: String,
    tillat: Tillat
): String {
    val severity = if (orgnummer.isNullOrEmpty()) "WARN" else "INFO"
    return "CEF:0|fia-api|auditLog|1.0|audit:${auditType.name}|fia-api|$severity|end=${System.currentTimeMillis()} " +
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


