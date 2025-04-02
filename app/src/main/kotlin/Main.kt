import application.AltinnTilgangerService
import db.createDataSource
import db.runMigration
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import plugins.configureMonitoring
import plugins.configureRouting
import plugins.configureSerialization

fun main() {
    createDataSource(Systemmiljø).also {
        Database.connect(it) // exposed
        runMigration(dataSource = it) // flyway
    }

    val altinnTilgangerService = AltinnTilgangerService()

    embeddedServer(
        factory = Netty,
        port = System.getenv("SERVER_PORT")?.toInt() ?: 8080,
        host = "0.0.0.0",
    ) {
        configure(altinnTilgangerService = altinnTilgangerService)
    }.start(wait = true)
}

fun Application.configure(altinnTilgangerService: AltinnTilgangerService) {
    configureMonitoring()
    configureSerialization()
    configureRouting(altinnTilgangerService = altinnTilgangerService)
}
