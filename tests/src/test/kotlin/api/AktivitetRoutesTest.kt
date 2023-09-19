package api

import api.dto.FullførtAktivitetJson
import api.endepunkt.json.OppdaterAktivitetJson
import container.helper.TestContainerHelper
import container.helper.withToken
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import request.ForebyggingsplanApi

internal class AktivitetRoutesTest : FunSpec({
    extension(TestContainerHelper.database)

    val forebyggingsplanApi = ForebyggingsplanApi(TestContainerHelper.forebyggingsplanContainer)
    val authorisertOrgnr = "811076732"
    val aktivitetsId = "aktivitetsid"
    val aktivitetsVersjon = "aktivitetsversjon"

    fun alleFelterLikeIgnorerDato(fullførtAktivitetJson: FullførtAktivitetJson) = Matcher<FullførtAktivitetJson> {
        MatcherResult(
            fullførtAktivitetJson.aktivitetsId == it.aktivitetsId &&
                    fullførtAktivitetJson.fullført == it.fullført,
            { "Objektene er ikke like. Fikk $it men forventet $fullførtAktivitetJson." },
            { "Objetene er like." },
        )
    }

    test("fullfør aktivitet returnerer 401 ved manglende autentisering") {
        val resultat = forebyggingsplanApi.fullførAktivitet(aktivitetsId, aktivitetsVersjon, authorisertOrgnr)

        resultat.status shouldBe HttpStatusCode.Unauthorized
    }

    test("fullfør aktivitet returnerer 403 hvis brukeren ikke har tilgang") {
        val resultat =
            forebyggingsplanApi.fullførAktivitet(aktivitetsId, aktivitetsVersjon, "999999999", withToken())

        resultat.status shouldBe HttpStatusCode.Forbidden
    }

    test("fullfør aktivitet returnerer 200 hvis aktiviteten blir fullført") {
        val resultat =
            forebyggingsplanApi.fullførAktivitet(aktivitetsId, aktivitetsVersjon, authorisertOrgnr, withToken())

        resultat.status shouldBe HttpStatusCode.OK
    }

    test("hent fullførte aktiviteter returnerer 401 ved manglende autentisering") {
        val resultat = forebyggingsplanApi.hentFullførteAktiviteter(authorisertOrgnr)

        resultat.status shouldBe HttpStatusCode.Unauthorized
    }

    test("hent fullførte aktivitetert returnerer 403 hvis brukeren ikke har tilgang") {
        val resultat =
            forebyggingsplanApi.hentFullførteAktiviteter("999999999", withToken())

        resultat.status shouldBe HttpStatusCode.Forbidden
    }

    test("hent fullførte aktiviteter returnerer 200 og en tom liste hvis ingen aktiviteter er fullført") {
        val resultat = forebyggingsplanApi.hentFullførteAktiviteter(authorisertOrgnr, withToken())

        resultat.status shouldBe HttpStatusCode.OK
        val fullførte: List<FullførtAktivitetJson> = resultat.body()
        fullførte shouldHaveSize 0
    }

    test("hent fullførte aktiviteter returnerer 200 og en liste med fullførte aktiviteter") {
        val fullført = FullførtAktivitetJson(
            aktivitetsId,
            true,
            Clock.System.now()
        )

        forebyggingsplanApi.fullførAktivitet(aktivitetsId, aktivitetsVersjon, authorisertOrgnr, withToken())
        val resultat = forebyggingsplanApi.hentFullførteAktiviteter(authorisertOrgnr, withToken())


        resultat.status shouldBe HttpStatusCode.OK
        val fullførte: List<FullførtAktivitetJson> = resultat.body()
        fullførte shouldHaveSize 1
        fullførte.first() shouldHave alleFelterLikeIgnorerDato(fullført)
    }

    test("oppdater svarer med 400 når payload er en tom JSON") {
        val resultat = forebyggingsplanApi.oppdater(
            authorisertOrgnr,
            aktivitetsId,
            withToken() {
                setBody("{}")
            })
        resultat.status shouldBe HttpStatusCode.BadRequest
    }

    test("oppdater svarer med 200 OK når en aktivitet er oppdatert") {
        val resultat = forebyggingsplanApi.oppdater(
            authorisertOrgnr,
            aktivitetsId,
            withToken() {
                setBody(OppdaterAktivitetJson(status = "fullført"))
            })
        resultat.bodyAsText() shouldBe ""
        resultat.status shouldBe HttpStatusCode.OK
    }

})