package container.api

import api.endepunkt.json.Aktivitetstype
import api.endepunkt.json.OppdaterAktivitetJson
import application.AltinnTilgangerService.Companion.ENKELRETTIGHET_ALTINN
import container.helper.TestContainerHelper
import container.helper.TestContainerHelper.Companion.altinnTilgangerContainerHelper
import container.helper.TestContainerHelper.Companion.enVirksomhet
import container.helper.TestContainerHelper.Companion.postgresContainerHelper
import container.helper.withToken
import container.helper.withoutToken
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlin.test.BeforeTest
import kotlin.test.Test

class AktivitetTest {
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

    private val aktivitetsId = "aktivitetsid"

    @Test
    fun `oppdater aktivitet svarer med 401 uten token`() {
        runBlocking {
            val resultat = TestContainerHelper.oppdaterAktivitet(
                orgnr = enVirksomhet.orgnr,
                aktivitetId = aktivitetsId,
                config = withoutToken(),
            )

            resultat.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 403 forbidden på forsøk mot en bedrift brukeren ikke har enkel rettighet til`() {
        runBlocking {
            val resultat = TestContainerHelper.oppdaterAktivitet(
                orgnr = "999999999",
                aktivitetId = aktivitetsId,
                config = withToken { setBody(OppdaterAktivitetJson(aktivitetstype = null, status = "FULLFØRT")) },
            )

            resultat.status shouldBe HttpStatusCode.Forbidden
        }
    }

    @Test
    fun `svarer med 400 når payload er en tom JSON`() {
        runBlocking {
            val resultat = TestContainerHelper.oppdaterAktivitet(
                orgnr = enVirksomhet.orgnr,
                aktivitetId = aktivitetsId,
                config = withToken { setBody("{}") },
            )

            resultat.status shouldBe HttpStatusCode.BadRequest
        }
    }

    @Test
    fun `svarer med 400 når status er ugyldig`() {
        runBlocking {
            val resultat = TestContainerHelper.oppdaterAktivitet(
                orgnr = enVirksomhet.orgnr,
                aktivitetId = aktivitetsId,
                config = withToken {
                    setBody(OppdaterAktivitetJson(aktivitetstype = null, status = "ikke_en_status_jeg_har_sett"))
                },
            )
            resultat.status shouldBe HttpStatusCode.BadRequest
        }
    }

    @Test
    fun `svarer med 200 OK når en oppgave er oppdatert og aktivitetstype er null`() {
        runBlocking {
            val resultat = TestContainerHelper.oppdaterAktivitet(
                orgnr = enVirksomhet.orgnr,
                aktivitetId = aktivitetsId,
                config = withToken {
                    setBody(OppdaterAktivitetJson(aktivitetstype = null, status = "fullført"))
                },
            )
            resultat.bodyAsText() shouldBe ""
            resultat.status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `svarer med 200 OK når en oppgave er oppdatert`() {
        runBlocking {
            val resultat = TestContainerHelper.oppdaterAktivitet(
                orgnr = enVirksomhet.orgnr,
                aktivitetId = aktivitetsId,
                config = withToken {
                    setBody(OppdaterAktivitetJson(aktivitetstype = Aktivitetstype.OPPGAVE, status = "fullført"))
                },
            )
            resultat.bodyAsText() shouldBe ""
            resultat.status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `svarer med 200 OK når en teoriseksjon er oppdatert`() {
        runBlocking {
            val resultat = TestContainerHelper.oppdaterAktivitet(
                orgnr = enVirksomhet.orgnr,
                aktivitetId = aktivitetsId,
                config = withToken {
                    setBody(OppdaterAktivitetJson(aktivitetstype = Aktivitetstype.TEORISEKSJON, status = "lest"))
                },
            )
            resultat.bodyAsText() shouldBe ""
            resultat.status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `svarer med 400 BadRequest når det er mismatch mellom status og aktivitetstype`() {
        runBlocking {
            val resultat = TestContainerHelper.oppdaterAktivitet(
                orgnr = enVirksomhet.orgnr,
                aktivitetId = aktivitetsId,
                config = withToken {
                    setBody(OppdaterAktivitetJson(aktivitetstype = Aktivitetstype.TEORISEKSJON, status = "fullført"))
                },
            )

            resultat.status shouldBe HttpStatusCode.BadRequest
        }
    }
}
