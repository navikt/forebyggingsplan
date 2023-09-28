package container.auth

import com.nimbusds.jwt.PlainJWT
import container.helper.TestContainerHelper
import container.helper.TestContainerHelper.Companion.performGet
import enVirksomhet
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

internal class AuthenticationTest {

    private suspend fun kallEndepunkt(orgnr: String = "1", block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
        return TestContainerHelper.forebyggingsplanContainer.performGet("/aktiviteter/orgnr/$orgnr", block)
    }

    @Test
    fun `skal få 401 på et token uten signatur`() {
        val accessToken = TestContainerHelper.accessToken()
        val plainToken = PlainJWT(accessToken.jwtClaimsSet) // Token med "alg": "none"
        runBlocking {
            kallEndepunkt {
                header(HttpHeaders.Authorization, "Bearer ${plainToken.serialize()}")
            }.status shouldBe HttpStatusCode.Unauthorized

            kallEndepunkt {
                // "alg": "n0ne"
                header(
                    HttpHeaders.Authorization,
                    "Bearer ewogICJhbGciOiAibjBuZSIKfQ.${plainToken.payload.toBase64URL()}"
                )
            }.status shouldBe HttpStatusCode.Unauthorized

            kallEndepunkt {
                // "alg": "nonE"
                header(
                    HttpHeaders.Authorization,
                    "Bearer ewogICJhbGciOiAibm9uRSIKfQ.${plainToken.payload.toBase64URL()}"
                )
            }.status shouldBe HttpStatusCode.Unauthorized

            kallEndepunkt {
                // "alg": "NONE"
                header(
                    HttpHeaders.Authorization,
                    "Bearer ewogICJhbGciOiAiTk9ORSIKfQ.${plainToken.payload.toBase64URL()}"
                )
            }.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 401 med ugyldig audience`() {
        val accessToken = TestContainerHelper.accessToken(audience = "ugyldig audience")

        runBlocking {
            kallEndepunkt {
                header(HttpHeaders.Authorization, "Bearer ${accessToken.serialize()}")
            }.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 401 uten acr satt til Level4`() {
        runBlocking {
            val gyldigToken = TestContainerHelper.accessToken()
            gyldigToken.jwtClaimsSet.getStringClaim("acr") shouldBe "Level4"

            kallEndepunkt(orgnr = enVirksomhet.orgnr) {
                header(HttpHeaders.Authorization, "Bearer ${gyldigToken.serialize()}")
            }.status shouldBe HttpStatusCode.OK

            val ugyldigToken = TestContainerHelper.accessToken(
                claims = mapOf(
                    "acr" to "Level3"
                )
            )
            ugyldigToken.jwtClaimsSet.getStringClaim("acr") shouldBe "Level3"

            kallEndepunkt {
                header(HttpHeaders.Authorization, "Bearer ${ugyldigToken.serialize()}")
            }.status shouldBe HttpStatusCode.Unauthorized

            val ugyldigToken2 = TestContainerHelper.accessToken(claims = emptyMap())
            ugyldigToken2.jwtClaimsSet.getStringClaim("acr") shouldBe null

            kallEndepunkt {
                header(HttpHeaders.Authorization, "Bearer ${ugyldigToken2.serialize()}")
            }.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 200 når ace er satt til idporten-loa-high`() {
        runBlocking {
            val gyldigToken = TestContainerHelper.accessToken(
                "123",
                "hei",
                mapOf(
                    "acr" to "idporten-loa-high",
                    "pid" to "123"
                ),
            )
            gyldigToken.jwtClaimsSet.getStringClaim("acr") shouldBe "idporten-loa-high"

            kallEndepunkt(orgnr = enVirksomhet.orgnr) {
                header(HttpHeaders.Authorization, "Bearer ${gyldigToken.serialize()}")
            }.status shouldBe HttpStatusCode.OK
        }
    }
}
