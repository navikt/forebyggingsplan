import api.endepunkt.Metrics
import api.endepunkt.fullførteAktiviteter
import api.endepunkt.helseEndepunkter
import api.endepunkt.metrics
import api.endepunkt.organisasjoner
import api.endepunkt.valgteAktiviteter
import com.auth0.jwk.JwkProviderBuilder
import db.AktivitetRepository
import db.DatabaseFactory
import exceptions.IkkeFunnetException
import exceptions.UgyldigForespørselException
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import plugins.AuthorizationPlugin
import java.net.URI
import java.util.concurrent.TimeUnit

fun main() {
    bootstrapServer()
}

fun bootstrapServer() {
    DatabaseFactory.init()
    val aktivitetService = AktivitetService(aktivitetRepository = AktivitetRepository())

    embeddedServer(factory = Netty, port = System.getenv("SERVER_PORT")?.toInt() ?: 8080) {
        install(ContentNegotiation) {
            json()
        }
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                when (cause) {
                    is IkkeFunnetException -> call.respond(status = HttpStatusCode.NotFound, message = cause.message!!)
                    is UgyldigForespørselException -> call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = cause.message!!
                    )

                    else -> {
                        this@embeddedServer.log.error("Uhåndtert feil", cause)
                        call.respond(status = HttpStatusCode.InternalServerError, "Uhåndtert feil")
                    }
                }
            }
        }
        install(MicrometerMetrics) {
            registry = Metrics.appMicrometerRegistry
        }
        val jwkProvider = JwkProviderBuilder(URI(Miljø.tokenxJwkPath).toURL())
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
        install(DoubleReceive)
        install(Authentication) {
            jwt(name = "tokenx") {
                val tokenFortsattGyldigFørUtløpISekunder = 3L
                verifier(jwkProvider, issuer = Miljø.tokenxIssuer) {
                    acceptLeeway(tokenFortsattGyldigFørUtløpISekunder)
                    withAudience(Miljø.tokenxClientId)
                    withClaim("acr", "Level4")
                    withClaimPresence("sub")
                }
                validate { token ->
                    JWTPrincipal(token.payload)
                }
            }
        }
        routing {
            helseEndepunkter()
            metrics()
            authenticate("tokenx") {
                organisasjoner()
                medAltinnTilgang {
                    valgteAktiviteter(aktivitetService = aktivitetService)
                    fullførteAktiviteter(aktivitetService = aktivitetService)
                }
            }
        }
    }.start(wait = true)
}

fun Route.medAltinnTilgang(authorizedRoutes: Route.() -> Unit) = createChild(selector).apply {
    install(AuthorizationPlugin)
    authorizedRoutes()
}
