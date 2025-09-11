package container.auth

import com.nimbusds.jwt.PlainJWT
import container.helper.TestContainerHelper
import container.helper.TestContainerHelper.Companion.altinnTilgangerContainerHelper
import container.helper.TestContainerHelper.Companion.enVirksomhet
import container.helper.TestContainerHelper.Companion.postgresContainerHelper
import container.helper.withToken
import io.kotest.matchers.shouldBe
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class AuthenticationTest {
    @BeforeTest
    fun cleanUp() {
        runBlocking {
            altinnTilgangerContainerHelper.slettAlleRettigheter()
            postgresContainerHelper.slettAlleAktiviteter()
        }
    }

    @Test
    fun `skal få 401 på et token uten signatur`() {
        val accessToken = TestContainerHelper.accessToken()
        val plainToken = PlainJWT(accessToken.jwtClaimsSet) // Token med "alg": "none"

        runBlocking {
            TestContainerHelper.hentAktiviteter(
                config = { header(HttpHeaders.Authorization, "Bearer ${plainToken.serialize()}") },
                orgnr = "1234",
            ).status shouldBe HttpStatusCode.Unauthorized

            TestContainerHelper.hentAktiviteter(orgnr = "1234", config = {
                // "alg": "n0ne"
                header(
                    HttpHeaders.Authorization,
                    "Bearer ewogICJhbGciOiAibjBuZSIKfQ.${plainToken.payload.toBase64URL()}",
                )
            }).status shouldBe HttpStatusCode.Unauthorized

            TestContainerHelper.hentAktiviteter(orgnr = "1234", config = {
                // "alg": "nonE"
                header(
                    HttpHeaders.Authorization,
                    "Bearer ewogICJhbGciOiAibm9uRSIKfQ.${plainToken.payload.toBase64URL()}",
                )
            }).status shouldBe HttpStatusCode.Unauthorized

            TestContainerHelper.hentAktiviteter(orgnr = "1234", config = {
                // "alg": "NONE"
                header(
                    HttpHeaders.Authorization,
                    "Bearer ewogICJhbGciOiAiTk9ORSIKfQ.${plainToken.payload.toBase64URL()}",
                )
            }).status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 401 med ugyldig audience`() {
        val accessToken = TestContainerHelper.accessToken(audience = "ugyldig audience")

        runBlocking {
            TestContainerHelper.hentAktiviteter(orgnr = "1234", config = {
                header(HttpHeaders.Authorization, "Bearer ${accessToken.serialize()}")
            }).status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 401 uten acr satt til Level4`() {
        runBlocking {
            val gyldigToken = TestContainerHelper.accessToken()
            gyldigToken.jwtClaimsSet.getStringClaim("acr") shouldBe "Level4"

            altinnTilgangerContainerHelper.leggTilRettighetIVirksomhet(
                underenhet = enVirksomhet.orgnr,
            )
            TestContainerHelper.hentAktiviteter(
                orgnr = enVirksomhet.orgnr,
                config = {
                    header(HttpHeaders.Authorization, "Bearer ${gyldigToken.serialize()}")
                },
            ).status shouldBe HttpStatusCode.OK

            val ugyldigToken = TestContainerHelper.accessToken(
                claims = mapOf(
                    "acr" to "Level3",
                ),
            )
            ugyldigToken.jwtClaimsSet.getStringClaim("acr") shouldBe "Level3"

            TestContainerHelper.hentAktiviteter(orgnr = "1234", config = {
                header(HttpHeaders.Authorization, "Bearer ${ugyldigToken.serialize()}")
            }).status shouldBe HttpStatusCode.Unauthorized

            val ugyldigToken2 = TestContainerHelper.accessToken(claims = emptyMap())
            ugyldigToken2.jwtClaimsSet.getStringClaim("acr") shouldBe null

            TestContainerHelper.hentAktiviteter(orgnr = "1234", config = {
                header(HttpHeaders.Authorization, "Bearer ${ugyldigToken2.serialize()}")
            }).status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 403 når man ikke har tilgang til virksomhet`() {
        runBlocking {
            val resultat = TestContainerHelper.hentAktiviteter(config = withToken(), orgnr = enVirksomhet.orgnr)
            resultat.status shouldBe HttpStatusCode.Forbidden
        }
    }

    @Test
    fun `skal få 200 når ace er satt til idporten-loa-high`() {
        altinnTilgangerContainerHelper.leggTilRettighetIVirksomhet(
            underenhet = enVirksomhet.orgnr,
        )
        runBlocking {
            val gyldigToken = TestContainerHelper.accessToken(
                "123",
                "hei",
                mapOf(
                    "acr" to "idporten-loa-high",
                    "pid" to "123",
                ),
            )
            gyldigToken.jwtClaimsSet.getStringClaim("acr") shouldBe "idporten-loa-high"

            TestContainerHelper.hentAktiviteter(
                orgnr = enVirksomhet.orgnr,
                config = {
                    header(HttpHeaders.Authorization, "Bearer ${gyldigToken.serialize()}")
                },
            ).status shouldBe HttpStatusCode.OK
        }
    }
}
