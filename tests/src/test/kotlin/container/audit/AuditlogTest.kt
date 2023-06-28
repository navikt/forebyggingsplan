package container.audit

import container.helper.TestContainerHelper.Companion.forebyggingsplanContainer
import container.helper.TestContainerHelper.Companion.shouldContainLog
import container.helper.withToken
import enVirksomhet
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import request.AktivitetApi

class AuditlogTest {
    private val aktivitetApi = AktivitetApi(forebyggingsplanContainer)

    @Test
    fun `auditlogger visning av valgte aktiviteter`() {
        runBlocking {
            hentValgteAktiviteterForVirksomhet().status shouldBe HttpStatusCode.OK
            forebyggingsplanContainer shouldContainLog "\\?audit=1".toRegex()
        }
    }

    @Test
    fun `auditlogger feil ved manglende orgnummer`() {
        runBlocking {
            hentValgteAktiviteterForVirksomhet("null").status shouldBe HttpStatusCode.BadRequest
            forebyggingsplanContainer shouldContainLog "\\?audit=1".toRegex()
        }
    }
    @Test
    fun `auditlogger feil ved feil i orgnummer`() {
        runBlocking {
            hentValgteAktiviteterForVirksomhet("1234").status shouldBe HttpStatusCode.BadRequest
            forebyggingsplanContainer shouldContainLog "ugyldig organisjasjonsnummer 1234 i requesten fra bruker ".toRegex()
        }
    }

    private suspend fun hentValgteAktiviteterForVirksomhet(orgnr: String = enVirksomhet.orgnr) = aktivitetApi.hentValgteAktiviteterForVirksomhet(
        orgnr = orgnr,
        block = withToken() { parameter("audit", "1") })

//    private fun auditLog(
//        requestMethod: String,
//        fnr: String?,
//        orgnummer: String,
//        auditType: AuditType,
//        tillat: Tillat,
//        severity: String = "INFO"
//    ) =
//        ("CEF:0\\|forebyggingsplan\\|auditLog\\|1.0\\|audit:${auditType.name}\\|fia-api\\|$severity\\|end=[0-9]+ " +
//                "suid=$orgnummer " +
//                (fnr?.let { "duid=$it " } ?: "") +
//                "sproc=.{26} " +
//                "requestMethod=$requestMethod " +
//                "request=.*?audit=1 " +
//                "flexString1Label=Decision " +
//                "flexString1=${tillat.tillat}")
}

//"""CEF:0|forebyggingsplan|auditLog|1.0|audit:access|forebyggingsplan|INFO|end=1669708473109 suid=123 duid=811076732 sproc=3a031446-fa3a-4be0-8abb-26b8a1812909 requestMethod=GET request=/valgteaktiviteter/811076732?audit=1""".toRegex()
