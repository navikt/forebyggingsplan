package container.api

import api.endepunkt.json.AktivitetJson
import api.endepunkt.json.Aktivitetstype
import api.endepunkt.json.OppdaterAktivitetJson
import application.AltinnTilgangerService.Companion.ENKELRETTIGHET_ALTINN
import container.helper.TestContainerHelper
import container.helper.TestContainerHelper.Companion.altinnTilgangerContainerHelper
import container.helper.TestContainerHelper.Companion.enVirksomhet
import container.helper.TestContainerHelper.Companion.postgresContainerHelper
import container.helper.withToken
import container.helper.withoutToken
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test

class AktiviteterTest {
    @BeforeTest
    fun cleanUp() {
        runBlocking {
            altinnTilgangerContainerHelper.slettAlleRettigheter()
            postgresContainerHelper.slettAlleStatistikk()
        }
    }

    @BeforeTest
    fun giTilgang() {
        altinnTilgangerContainerHelper.leggTilRettigheter(
            underenhet = enVirksomhet.orgnr,
            altinn2Rettighet = ENKELRETTIGHET_ALTINN,
        )
    }

    @Test
    fun `svarer med 401 hvis ikke vi er logget inn`() {
        val tulleorgnr = "123456789"

        runBlocking {
            val resultat = TestContainerHelper.hentAktiviteter(config = withoutToken(), orgnr = tulleorgnr)

            resultat.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `svarer med 200 OK med tom JSON-array hvis brukeren ikke har noen aktiviteter`() {
        runBlocking {
            val resultat = TestContainerHelper.hentAktiviteter(
                withToken(),
                enVirksomhet.orgnr,
            )

            resultat.bodyAsText() shouldBe "[]"
            resultat.status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `svarer med 200 OK med oppgaver`() {
        runBlocking {
            val aktivitetId = "123"
            val aktivitetJson = AktivitetJson(
                aktivitetId = aktivitetId,
                aktivitetType = Aktivitetstype.OPPGAVE,
                status = AktivitetJson.Status.STARTET,
            )

            TestContainerHelper.oppdaterAktivitet(
                orgnr = enVirksomhet.orgnr,
                aktivitetId = aktivitetId,
                config = withToken {
                    setBody(
                        OppdaterAktivitetJson(
                            aktivitetstype = null,
                            status = "STARTET",
                        ),
                    )
                },
            )

            val resultat = TestContainerHelper.hentAktiviteter(config = withToken(), orgnr = enVirksomhet.orgnr)

            resultat.status shouldBe HttpStatusCode.OK

            val body = resultat.body<List<AktivitetJson>>()
            body shouldContainExactly listOf(aktivitetJson)
        }
    }

    @Test
    fun `svarer med 200 OK med teoriseksjoner`() {
        runBlocking {
            val aktivitetId = "123"
            val aktivitetJson = AktivitetJson(
                aktivitetId = aktivitetId,
                aktivitetType = Aktivitetstype.TEORISEKSJON,
                status = AktivitetJson.Status.LEST,
            )

            TestContainerHelper.oppdaterAktivitet(
                orgnr = enVirksomhet.orgnr,
                aktivitetId = aktivitetId,
                config = withToken {
                    setBody(
                        OppdaterAktivitetJson(
                            aktivitetstype = Aktivitetstype.TEORISEKSJON,
                            status = "LEST",
                        ),
                    )
                },
            )

            val resultat = TestContainerHelper.hentAktiviteter(config = withToken(), orgnr = enVirksomhet.orgnr)

            resultat.status shouldBe HttpStatusCode.OK

            val body = resultat.body<List<AktivitetJson>>()
            body shouldContainExactly listOf(aktivitetJson)
        }
    }

    @Test
    fun `svarer med 200 OK med alle typer aktiviteter`() {
        runBlocking {
            val teoriseksjonId = "123"
            val oppgaveId = "456"
            val teoriseksjon = AktivitetJson(
                aktivitetId = teoriseksjonId,
                aktivitetType = Aktivitetstype.TEORISEKSJON,
                status = AktivitetJson.Status.LEST,
            )
            val oppgave = AktivitetJson(
                aktivitetId = oppgaveId,
                aktivitetType = Aktivitetstype.OPPGAVE,
                status = AktivitetJson.Status.FULLFØRT,
            )
            TestContainerHelper.oppdaterAktivitet(
                orgnr = enVirksomhet.orgnr,
                aktivitetId = teoriseksjonId,
                config = withToken {
                    setBody(
                        OppdaterAktivitetJson(
                            aktivitetstype = Aktivitetstype.TEORISEKSJON,
                            status = "LEST",
                        ),
                    )
                },
            )
            TestContainerHelper.oppdaterAktivitet(
                orgnr = enVirksomhet.orgnr,
                aktivitetId = oppgaveId,
                config = withToken {
                    setBody(
                        OppdaterAktivitetJson(
                            aktivitetstype = Aktivitetstype.OPPGAVE,
                            status = "FULLFØRT",
                        ),
                    )
                },
            )

            val resultat = TestContainerHelper.hentAktiviteter(config = withToken(), orgnr = enVirksomhet.orgnr)

            resultat.status shouldBe HttpStatusCode.OK

            val body = resultat.body<List<AktivitetJson>>()
            body shouldContainExactly listOf(teoriseksjon, oppgave)
        }
    }
}
