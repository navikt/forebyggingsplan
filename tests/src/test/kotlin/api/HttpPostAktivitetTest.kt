package api

import api.endepunkt.json.OppdaterAktivitetJson
import container.helper.TestContainerHelper
import container.helper.withToken
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.security.mock.oauth2.http.json
import request.ForebyggingsplanApi

internal class HttpPostAktivitetTest : FunSpec({
    extension(TestContainerHelper.database)

    val forebyggingsplanApi = ForebyggingsplanApi(TestContainerHelper.forebyggingsplanContainer)
    val authorisertOrgnr = "811076732"
    val aktivitetsId = "aktivitetsid"

    context("oppdater aktivitet") {
        test("svarer med 401 uten token") {
            val resultat = forebyggingsplanApi.oppdater(
                authorisertOrgnr,
                aktivitetsId
            ) { header(HttpHeaders.Authorization, "Bearer ") }
            resultat.status shouldBe HttpStatusCode.Unauthorized
        }

        test("skal få 403 forbidden på forsøk mot en bedrift brukeren ikke har enkel rettighet til") {
            val resultat = forebyggingsplanApi.oppdater(
                orgnr = "999999999",
                aktivitetId = aktivitetsId,
                block = withToken { setBody(OppdaterAktivitetJson(status = "FULLFØRT")) }
            )
            resultat.status shouldBe HttpStatusCode.Forbidden
        }

        test("svarer med 400 når payload er en tom JSON") {
            val resultat = forebyggingsplanApi.oppdater(
                authorisertOrgnr,
                aktivitetsId,
                withToken {
                    setBody("{}")
                })
            resultat.status shouldBe HttpStatusCode.BadRequest
        }

        test("svarer med 400 når status er ugyldig") {
            val resultat = forebyggingsplanApi.oppdater(
                authorisertOrgnr,
                aktivitetsId,
                withToken {
                    setBody(OppdaterAktivitetJson(status = "ikke_en_status_jeg_har_sett"))
                })
            resultat.status shouldBe HttpStatusCode.BadRequest
        }

        test("svarer med 200 OK når en aktivitet er oppdatert") {
            val resultat = forebyggingsplanApi.oppdater(
                authorisertOrgnr,
                aktivitetsId,
                withToken {
                    setBody(OppdaterAktivitetJson(status = "fullført"))
                })
            resultat.bodyAsText() shouldBe ""
            resultat.status shouldBe HttpStatusCode.OK
        }
    }
})