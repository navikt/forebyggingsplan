package container.helper

import DbMiljø
import db.createDataSource
import db.runMigration
import org.jetbrains.exposed.sql.Database
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource

class PostgresContainerHelper(
    network: Network = Network.newNetwork(),
    log: Logger = LoggerFactory.getLogger(PostgresContainerHelper::class.java),
) {
    private val postgresNetworkAlias = "postgrescontainer"
    internal val dbName = "forebyggingsplan"
    private var migreringErKjørt = false
    private val port = 5432
    val container: PostgreSQLContainer<*> =
        PostgreSQLContainer(DockerImageName.parse("postgres:14.5"))
            .withLogConsumer(
                Slf4jLogConsumer(log).withPrefix(postgresNetworkAlias).withSeparateOutputStreams(),
            )
            .withNetwork(network)
            .withNetworkAliases(postgresNetworkAlias)
            .withDatabaseName(dbName)
            .withExposedPorts(port)
            .withCreateContainerCmdModifier { cmd -> cmd.withName("$postgresNetworkAlias-${System.currentTimeMillis()}") }
            .waitingFor(HostPortWaitStrategy()).apply {
                start()
            }

    private val dataSource = nyDataSource()

    private fun nyDataSource(): DataSource {
        val dbMiljø = object : DbMiljø {
            override val dbHost: String = container.host
            override val dbDatabaseName: String = dbName
            override val dbPort: String = container.getMappedPort(port).toString()
            override val dbUser: String = container.username
            override val dbPassword: String = container.password
        }

        val dataSource = createDataSource(dbMiljø).also {
            Database.connect(it)
            if (!migreringErKjørt) {
                runMigration(it)
                migreringErKjørt = true
            }
        }

        return dataSource
    }

    private fun slettAllData(table: String) =
        dataSource.connection.use { connection ->
            val statement = connection.createStatement()
            statement.execute("DELETE FROM $table")
        }

    fun slettAlleAktiviteter() {
        for (tabell in listOf(
            "valgtaktivitet",
            "aktiviteter",
        )) {
            slettAllData(tabell)
        }
    }

    fun envVars() =
        mapOf(
            "DB_HOST" to postgresNetworkAlias,
            "DB_DATABASE" to dbName,
            "DB_PORT" to "5432",
            "DB_USERNAME" to container.username,
            "DB_PASSWORD" to container.password,
        )
}
