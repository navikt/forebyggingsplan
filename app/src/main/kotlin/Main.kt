import api.endepunkt.*
import com.auth0.jwk.JwkProviderBuilder
import db.AktivitetRepository
import db.DatabaseFactory
import exceptions.IkkeFunnetException
import exceptions.UgyldigForespørselException
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.uri
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
                    application.log.info("request uri ${request.uri} in validate block")
                    application.log.info("headers ${request.headers}")
                    JWTPrincipal(token.payload)
                }
            }
        }
        routing {
            helseEndepunkter()
            metrics()
            authenticate("tokenx") {
                aktivitetsmaler(aktivitetService = aktivitetService)
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
