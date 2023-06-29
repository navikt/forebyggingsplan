package container.helper

import DbMiljø
import db.AktiviteterRepository
import db.DatabaseFactory
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.utility.DockerImageName

class PostgresContainer(network: Network = Network.newNetwork()) : BeforeAllCallback, AfterEachCallback, BeforeSpecListener, AfterEachListener {
    internal val postgresNetworkAlias = "postgrescontainer"
    internal val dbName = "forebyggingsplan"
    private val port = 5432
    internal val container = PostgreSQLContainer(DockerImageName.parse("postgres:14.5"))
        .withDatabaseName(dbName)
        .withNetworkAliases(postgresNetworkAlias)
        .withNetwork(network)
        .withExposedPorts(port)
        .waitingFor(HostPortWaitStrategy())
        .apply {
            start()
        }

    override suspend fun beforeSpec(spec: Spec) {
        beforeAll(null)
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        afterEach(null)
    }

    override fun beforeAll(context: ExtensionContext?) {
        val dbMiljø = object : DbMiljø {
            override val dbHost: String = container.host
            override val dbDatabaseName: String = dbName
            override val dbPort: String = container.getMappedPort(port).toString()
            override val dbUser: String = container.username
            override val dbPassword: String = container.password
        }
        DatabaseFactory(dbMiljø).init()
    }

    override fun afterEach(context: ExtensionContext?) {
        AktiviteterRepository.slettAlt()
    }


    private fun AktiviteterRepository.slettAlt() {
        transaction {
            deleteAll()
        }
    }
}
