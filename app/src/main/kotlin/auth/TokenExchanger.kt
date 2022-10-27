package auth

import Miljø
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.nimbusds.jose.jwk.RSAKey
import http.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters
import org.slf4j.LoggerFactory
import java.net.URI
import java.time.Instant
import java.util.Date
import java.util.UUID

object TokenExchanger {
    private val privateKey = RSAKey.parse(Miljø.tokenxPrivateJwk).toRSAPrivateKey()
    private val logger = LoggerFactory.getLogger(this.javaClass)

    internal suspend fun exchangeToken(token: String, audience: String): String {
        return try {
            HttpClient.client.post(URI.create(Miljø.tokenXTokenEndpoint).toURL()) {
                val now = Instant.now()
                val clientAssertion = JWT.create().apply {
                    withSubject(Miljø.tokenxClientId)
                    withIssuer(Miljø.tokenxClientId)
                    withAudience(Miljø.tokenXTokenEndpoint)
                    withJWTId(UUID.randomUUID().toString())
                    withIssuedAt(Date.from(now))
                    withNotBefore(Date.from(now))
                    withExpiresAt(Date.from(now.plusSeconds(120)))
                }.sign(Algorithm.RSA256(null, privateKey))

                setBody(FormDataContent(Parameters.build {
                    append("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
                    append("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                    append("client_assertion", clientAssertion)
                    append("subject_token_type", "urn:ietf:params:oauth:token-type:jwt")
                    append("subject_token", token)
                    append("audience", audience)
                }))
            }.body<Map<String, String>>()["access_token"] ?: throw IllegalStateException("Fikk ingen token i response")
        } catch (e: Exception) {
            throw RuntimeException("Token exchange feil", e)
        }
    }
}