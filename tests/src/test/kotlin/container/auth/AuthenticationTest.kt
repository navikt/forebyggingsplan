package container.auth

import api.dto.ValgtAktivitetDTO
import com.nimbusds.jwt.PlainJWT
import container.helper.TestContainerHelper
import container.helper.withToken
import enVirksomhet
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import request.AktivitetApi
import java.util.*

internal class AuthenticationTest {

    private val aktivitetApi = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    fun `happy path - skal få 200 ok ved henting av aktivitet`() {
        runBlocking {
            aktivitetApi.velgAktivitet(
                aktivitetsmalId = UUID.randomUUID().toString(),
                orgnr = enVirksomhet.orgnr,
                block = withToken()
            )
            val response =
                aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr, block = withToken())
            response.status shouldBe HttpStatusCode.OK
            response.body<List<ValgtAktivitetDTO>>() shouldHaveAtLeastSize 1
        }
    }

    @Test
    fun `skal få 401 unauthorized på kall uten token`() {
        runBlocking {
            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = "1").status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 403 forbidden på forsøk mot en bedrift brukeren ikke har enkel rettighet til`() {
        runBlocking {
            aktivitetApi.hentValgteAktiviteterForVirksomhet(
                orgnr = "315829062",
                block = withToken()
            ).status shouldBe HttpStatusCode.Forbidden
        }
    }

    @Test
    fun `skal få 401 på et token uten signatur`() {
        val accessToken = TestContainerHelper.accessToken()
        val plainToken = PlainJWT(accessToken.jwtClaimsSet) // Token med "alg": "none"
        runBlocking {
            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = "1") {
                header(HttpHeaders.Authorization, "Bearer ${plainToken.serialize()}")
            }.status shouldBe HttpStatusCode.Unauthorized
            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = "1") {
                // "alg": "n0ne"
                header(
                    HttpHeaders.Authorization,
                    "Bearer ewogICJhbGciOiAibjBuZSIKfQ.${plainToken.payload.toBase64URL()}"
                )
            }.status shouldBe HttpStatusCode.Unauthorized
            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = "1") {
                // "alg": "nonE"
                header(
                    HttpHeaders.Authorization,
                    "Bearer ewogICJhbGciOiAibm9uRSIKfQ.${plainToken.payload.toBase64URL()}"
                )
            }.status shouldBe HttpStatusCode.Unauthorized
            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = "1") {
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
            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = "1") {
                header(HttpHeaders.Authorization, "Bearer ${accessToken.serialize()}")
            }.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `skal få 401 uten acr satt til Level4`() {
        runBlocking {
            val gyldigToken = TestContainerHelper.accessToken()
            gyldigToken.jwtClaimsSet.getStringClaim("acr") shouldBe "Level4"

            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr) {
                header(HttpHeaders.Authorization, "Bearer ${gyldigToken.serialize()}")
            }.status shouldBe HttpStatusCode.OK

            val ugyldigToken = TestContainerHelper.accessToken(
                claims = mapOf(
                    "acr" to "Level3"
                )
            )
            ugyldigToken.jwtClaimsSet.getStringClaim("acr") shouldBe "Level3"

            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = "1") {
                header(HttpHeaders.Authorization, "Bearer ${ugyldigToken.serialize()}")
            }.status shouldBe HttpStatusCode.Unauthorized

            val ugyldigToken2 = TestContainerHelper.accessToken(claims = emptyMap())
            ugyldigToken2.jwtClaimsSet.getStringClaim("acr") shouldBe null

            aktivitetApi.hentValgteAktiviteterForVirksomhet("1") {
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

            aktivitetApi.hentValgteAktiviteterForVirksomhet(orgnr = enVirksomhet.orgnr) {
                header(HttpHeaders.Authorization, "Bearer ${gyldigToken.serialize()}")
            }.status shouldBe HttpStatusCode.OK
        }
    }
}
