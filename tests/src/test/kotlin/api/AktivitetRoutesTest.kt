package api

import container.helper.TestContainerHelper
import container.helper.withToken
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import request.ForebyggingsplanApi

internal class AktivitetRoutesTest : FunSpec({
    val forebyggingsplanApi = ForebyggingsplanApi(TestContainerHelper.forebyggingsplanContainer)
    val authorisertOrgnr = "811076732"

    test("fullfør aktivitet returnerer 401 ved manglende autentisering") {
        val resultat = forebyggingsplanApi.fullførAktivitet("aktivitetsid", "aktivitetsversjon", authorisertOrgnr)

        resultat.status shouldBe HttpStatusCode.Unauthorized
    }

    test("fullfør aktivitet returnerer 403 hvis brukeren ikke har tilgang") {
        val resultat = forebyggingsplanApi.fullførAktivitet("aktivitetsid", "aktivitetsversjon", "999999999", withToken())

        resultat.status shouldBe HttpStatusCode.Forbidden
    }

    test("fullfør aktivitet returnerer 200 hvis aktiviteten blir fullført") {
        val resultat = forebyggingsplanApi.fullførAktivitet("aktivitetsid", "aktivitetsversjon", authorisertOrgnr, withToken())

        resultat.status shouldBe HttpStatusCode.OK
    }
})