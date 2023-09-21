package api

import api.dto.AktivitetJson
import api.endepunkt.json.OppdaterAktivitetJson
import container.helper.TestContainerHelper
import container.helper.TestContainerHelper.Companion.performGet
import container.helper.TestContainerHelper.Companion.performPost
import container.helper.withToken
import enVirksomhet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class httpGetAktiviteterTest : FunSpec({
    extension(TestContainerHelper.database)

    val autorisertOrgnr = enVirksomhet.orgnr

    context("hent aktiviteter") {
        test("svarer med 401 hvis ikke vi er logget inn") {
            val resultat =
                TestContainerHelper.forebyggingsplanContainer.performGet("/aktiviteter/orgnr/123456789")

            resultat.status shouldBe HttpStatusCode.Unauthorized
        }

        test("svarer med 200 OK med tom JSON-array hvis brukeren ikke har noen aktiviteter") {
            val resultat = TestContainerHelper.forebyggingsplanContainer.performGet(
                "/aktiviteter/orgnr/$autorisertOrgnr",
                withToken()
            )

            resultat.bodyAsText() shouldBe "[]"
            resultat.status shouldBe HttpStatusCode.OK
        }

        test("svarer med 200 OK med oppgaver") {
            val aktivitetId = "123"
            val aktivitetJson = AktivitetJson(
                aktivitetId = aktivitetId,
                aktivitetType = AktivitetJson.Aktivitetstype.OPPGAVE,
                status = AktivitetJson.Oppgavestatus.STARTET,
            )
            TestContainerHelper.forebyggingsplanContainer.performPost(
                "/aktivitet/$aktivitetId/orgnr/$autorisertOrgnr/oppdater",
                withToken {
                    setBody(OppdaterAktivitetJson(status = "STARTET"))
                })
            val resultat = TestContainerHelper.forebyggingsplanContainer.performGet(
                "/aktiviteter/orgnr/$autorisertOrgnr",
                withToken()
            )

            resultat.status shouldBe HttpStatusCode.OK

            val body = resultat.body<List<AktivitetJson>>()
            body shouldContainExactly listOf(aktivitetJson)
        }
    }
})