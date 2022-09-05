package container.auth

import com.nimbusds.jwt.PlainJWT
import container.helper.TestContainerHelper
import container.helper.withToken
import io.kotest.matchers.shouldBe
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import request.AktivitetApi
import kotlin.test.Test

internal class AuthenticationTest {

    private val aktivitetApi = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    fun `happy path - skal få 200 ok ved henting av aktivitet`() {
        runBlocking {
            aktivitetApi.hentAktiviteter(withToken()).status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `skal få 401 unauthorized på kall uten token`() {
        runBlocking {
            aktivitetApi.hentAktiviteter().status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 401 på et token uten signatur`() {
        val accessToken = TestContainerHelper.accessToken()
        val plainToken = PlainJWT(accessToken.jwtClaimsSet) // Token med "alg": "none"
        runBlocking {
            aktivitetApi.hentAktiviteter {
                header(HttpHeaders.Authorization, "Bearer ${plainToken.serialize()}")
            }.status shouldBe HttpStatusCode.Unauthorized
            aktivitetApi.hentAktiviteter {
                // "alg": "n0ne"
                header(HttpHeaders.Authorization, "Bearer ewogICJhbGciOiAibjBuZSIKfQ.${plainToken.payload.toBase64URL()}")
            }.status shouldBe HttpStatusCode.Unauthorized
            aktivitetApi.hentAktiviteter {
                // "alg": "nonE"
                header(HttpHeaders.Authorization, "Bearer ewogICJhbGciOiAibm9uRSIKfQ.${plainToken.payload.toBase64URL()}")
            }.status shouldBe HttpStatusCode.Unauthorized
            aktivitetApi.hentAktiviteter {
                // "alg": "NONE"
                header(HttpHeaders.Authorization, "Bearer ewogICJhbGciOiAiTk9ORSIKfQ.${plainToken.payload.toBase64URL()}")
            }.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 401 med ugyldig audience`() {
        val accessToken = TestContainerHelper.accessToken(audience = "ugyldig audience")

        runBlocking {
            aktivitetApi.hentAktiviteter {
                header(HttpHeaders.Authorization, "Bearer ${accessToken.serialize()}")
            }.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 401 uten acr satt til Level4`() {
        runBlocking {
            val gyldigToken = TestContainerHelper.accessToken()
            gyldigToken.jwtClaimsSet.getStringClaim("acr") shouldBe "Level4"

            aktivitetApi.hentAktiviteter {
                header(HttpHeaders.Authorization, "Bearer ${gyldigToken.serialize()}")
            }.status shouldBe HttpStatusCode.OK

            val ugyldigToken = TestContainerHelper.accessToken(claims = mapOf(
                "acr" to "Level3"
            ))
            ugyldigToken.jwtClaimsSet.getStringClaim("acr") shouldBe "Level3"

            aktivitetApi.hentAktiviteter {
                header(HttpHeaders.Authorization, "Bearer ${ugyldigToken.serialize()}")
            }.status shouldBe HttpStatusCode.Unauthorized

            val ugyldigToken2 = TestContainerHelper.accessToken(claims = emptyMap())
            ugyldigToken2.jwtClaimsSet.getStringClaim("acr") shouldBe null

            aktivitetApi.hentAktiviteter {
                header(HttpHeaders.Authorization, "Bearer ${ugyldigToken2.serialize()}")
            }.status shouldBe HttpStatusCode.Unauthorized
        }
    }
}