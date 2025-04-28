package container.audit

import application.AltinnTilgangerService.Companion.ENKELRETTIGHET_FOREBYGGE_FRAVÆR_ALTINN_3
import container.helper.TestContainerHelper
import container.helper.TestContainerHelper.Companion.altinnTilgangerContainerHelper
import container.helper.TestContainerHelper.Companion.applikasjon
import container.helper.TestContainerHelper.Companion.enVirksomhet
import container.helper.TestContainerHelper.Companion.postgresContainerHelper
import container.helper.TestContainerHelper.Companion.shouldContainLog
import container.helper.withToken
import io.kotest.matchers.shouldBe
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test

class AuditlogTest {
    @BeforeTest
    fun cleanUp() {
        runBlocking {
            altinnTilgangerContainerHelper.slettAlleRettigheter()
            postgresContainerHelper.slettAlleAktiviteter()
        }

        altinnTilgangerContainerHelper.leggTilRettigheter(
            underenhet = enVirksomhet.orgnr,
            altinn3Rettighet = ENKELRETTIGHET_FOREBYGGE_FRAVÆR_ALTINN_3,
        )
    }

    @Test
    fun `auditlogger visning av valgte aktiviteter`() {
        runBlocking {
            val resultat = TestContainerHelper.hentAktiviteter(
                orgnr = enVirksomhet.orgnr,
                config = withToken { parameter("parameter", "1") },
            )

            resultat.status shouldBe HttpStatusCode.OK
            applikasjon shouldContainLog "\\?parameter=1".toRegex()
        }
    }

    @Test
    fun `auditlogger feil ved manglende orgnummer`() {
        runBlocking {
            val resultat = TestContainerHelper.hentAktiviteter(
                orgnr = "null",
                config = withToken { parameter("parameter", "1") },
            )

            resultat.status shouldBe HttpStatusCode.BadRequest

            applikasjon shouldContainLog "\\?parameter=1".toRegex()
        }
    }

    @Test
    fun `auditlogger feil ved feil i orgnummer`() {
        runBlocking {
            val resultat = TestContainerHelper.hentAktiviteter(
                orgnr = "1234",
                config = withToken(),
            )

            resultat.status shouldBe HttpStatusCode.BadRequest
            applikasjon shouldContainLog "ugyldig organisjasjonsnummer 1234 i requesten fra bruker ".toRegex()
        }
    }
}
