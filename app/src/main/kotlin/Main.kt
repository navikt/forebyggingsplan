import api.endepunkt.*
import application.AktivitetService
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import db.ValgtAktivitetRepository
import db.DatabaseFactory
import db.SqlAktiviteterRepository
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
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import plugins.AuthorizationPlugin
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
        module = Application::forebyggingsplanApplicationModule
    ).start(wait = true)
}

fun Route.medAltinnTilgang(authorizedRoutes: Route.() -> Unit) = createChild(selector).apply {
    install(AuthorizationPlugin)
    authorizedRoutes()
}

fun Application.forebyggingsplanApplicationModule() {
    val legacyAktivitetService = LegacyAktivitetService(aktivitetRepository = ValgtAktivitetRepository())
    val aktivitetService = AktivitetService(aktivitetRepository = SqlAktiviteterRepository)

    install(ContentNegotiation) {
        json()
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is IkkeFunnetException -> call.respond(status = HttpStatusCode.NotFound, message = cause.message!!)
                is UgyldigForespørselException -> call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = cause.message
                )

                is BadRequestException -> call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = cause.message!!
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
                    claim.asString().equals("Level4") || claim.asString().equals("idporten-loa-high")
                }
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
                valgteAktiviteter(aktivitetService = legacyAktivitetService)
                fullførteAktiviteter(aktivitetService = legacyAktivitetService)
                fullførAktivitet(aktivitetService = aktivitetService)
            }
        }
    }
}
