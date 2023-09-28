package container.audit

import container.helper.TestContainerHelper.Companion.forebyggingsplanContainer
import container.helper.TestContainerHelper.Companion.performGet
import container.helper.TestContainerHelper.Companion.performPost
import container.helper.TestContainerHelper.Companion.shouldContainLog
import container.helper.withToken
import enVirksomhet
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import request.ForebyggingsplanApi

class AuditlogTest {
    @Test
    fun `auditlogger visning av valgte aktiviteter`() {
        runBlocking {
            val resultat =
                forebyggingsplanContainer.performGet(
                    "/aktiviteter/orgnr/${enVirksomhet.orgnr}",
                    withToken { parameter("parameter", "1") }
                )

            resultat.status shouldBe HttpStatusCode.OK
            forebyggingsplanContainer shouldContainLog "\\?parameter=1".toRegex()
        }
    }

    @Test
    fun `auditlogger feil ved manglende orgnummer`() {
        runBlocking {
            forebyggingsplanContainer.performGet("/aktiviteter/orgnr/null", withToken { parameter("parameter", "1") })
                .status shouldBe HttpStatusCode.BadRequest

            forebyggingsplanContainer shouldContainLog "\\?parameter=1".toRegex()
        }
    }

    @Test
    fun `auditlogger feil ved feil i orgnummer`() {
        runBlocking {
            val resultat = forebyggingsplanContainer.performGet("/aktiviteter/orgnr/1234", withToken())
            resultat.status shouldBe HttpStatusCode.BadRequest
            forebyggingsplanContainer shouldContainLog "ugyldig organisjasjonsnummer 1234 i requesten fra bruker ".toRegex()
        }
    }
}