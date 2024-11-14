import api.endepunkt.Metrics
import api.endepunkt.aktivitet
import api.endepunkt.aktiviteter
import api.endepunkt.helseEndepunkter
import api.endepunkt.metrics
import api.endepunkt.organisasjoner
import application.AktivitetService
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import db.DatabaseFactory
import db.SqlAktiviteterRepository
import exceptions.IkkeFunnetException
import exceptions.UgyldigForespørselException
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.IgnoreTrailingSlash
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingNode
import io.ktor.server.routing.routing
import plugins.AuthorizationPlugin
import util.hash.Sha3Hasher
import java.net.URI
import java.util.concurrent.TimeUnit

fun main() {
    bootstrapServer()
}

fun bootstrapServer() {
    DatabaseFactory(Systemmiljø).init()

    embeddedServer(
        factory = Netty,
        port = System.getenv("SERVER_PORT")?.toInt() ?: 8080,
        module = Application::forebyggingsplanApplicationModule,
    ).start(wait = true)
}

fun Route.medAltinnTilgang(authorizedRoutes: Route.() -> Unit) =
    (this as RoutingNode).createChild(selector).apply {
        install(AuthorizationPlugin)
        authorizedRoutes()
    }

fun Application.forebyggingsplanApplicationModule() {
    val hasher = Sha3Hasher()
    val aktivitetService =
        AktivitetService(aktivitetRepository = SqlAktiviteterRepository, hasher = hasher)

    install(ContentNegotiation) {
        json()
    }
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
                    this@forebyggingsplanApplicationModule.log.error("Uhåndtert feil", cause)
                    call.respond(status = HttpStatusCode.InternalServerError, "Uhåndtert feil")
                }
            }
        }
    }
    install(MicrometerMetrics) {
        registry = Metrics.appMicrometerRegistry
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
        metrics()
        authenticate("tokenx") {
            organisasjoner()
            medAltinnTilgang {
                aktiviteter(aktivitetService = aktivitetService)
                aktivitet(aktivitetService = aktivitetService, hasher = hasher)
            }
        }
    }
}
