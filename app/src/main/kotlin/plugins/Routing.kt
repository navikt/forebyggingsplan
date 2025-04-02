package plugins

import api.endepunkt.aktivitet
import api.endepunkt.aktiviteter
import api.endepunkt.helseEndepunkter
import application.AktivitetService
import application.AltinnTilgangerService
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import db.SqlAktiviteterRepository
import exceptions.IkkeFunnetException
import exceptions.UgyldigForespørselException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.IgnoreTrailingSlash
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingNode
import io.ktor.server.routing.routing
import util.hash.Sha3Hasher
import java.net.URI
import java.util.concurrent.TimeUnit

fun Route.medAltinnTilgang(
    altinnTilgangerService: AltinnTilgangerService,
    authorizedRoutes: Route.() -> Unit,
) = (this as RoutingNode).createChild(selector).apply {
    install(
        AltinnAuthorizationPlugin(
            altinnTilgangerService = altinnTilgangerService,
        ),
    )
    authorizedRoutes()
}

fun Application.configureRouting(altinnTilgangerService: AltinnTilgangerService) {
    val hasher = Sha3Hasher()
    val aktivitetService =
        AktivitetService(aktivitetRepository = SqlAktiviteterRepository, hasher = hasher)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is IkkeFunnetException -> call.respond(
                    status = HttpStatusCode.NotFound,
                    message = cause.message!!,
                )

                is UgyldigForespørselException -> call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = cause.message,
                )

                is BadRequestException -> call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = cause.message!!,
                )

                else -> {
                    this@configureRouting.log.error("Uhåndtert feil", cause)
                    call.respond(status = HttpStatusCode.InternalServerError, "Uhåndtert feil")
                }
            }
        }
    }
    val jwkProvider = JwkProviderBuilder(URI(Systemmiljø.tokenxJwkPath).toURL())
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()
    install(DoubleReceive)
    install(Authentication) {
        jwt(name = "tokenx") {
            val tokenFortsattGyldigFørUtløpISekunder = 3L
            verifier(jwkProvider, issuer = Systemmiljø.tokenxIssuer) {
                acceptLeeway(tokenFortsattGyldigFørUtløpISekunder)
                withAudience(Systemmiljø.tokenxClientId)
                withClaim("acr") { claim: Claim, _: DecodedJWT ->
                    claim.asString().equals("Level4") ||
                        claim.asString()
                            .equals("idporten-loa-high")
                }
                withClaimPresence("sub")
            }
            validate { token ->
                JWTPrincipal(token.payload)
            }
        }
    }
    install(IgnoreTrailingSlash)
    routing {
        helseEndepunkter()
        authenticate("tokenx") {
            medAltinnTilgang(
                altinnTilgangerService = altinnTilgangerService,
            ) {
                aktiviteter(aktivitetService = aktivitetService)
                aktivitet(aktivitetService = aktivitetService, hasher = hasher)
            }
        }
    }
}
