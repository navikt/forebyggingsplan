package container.helper

import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.utility.DockerImageName

class PostgresContainer(network: Network = Network.newNetwork()) {
    internal val postgresNetworkAlias = "postgrescontainer"
    internal val dbName = "forebyggingsplan"
    internal val container = PostgreSQLContainer(DockerImageName.parse("postgres:14.5"))
        .withDatabaseName(dbName)
        .withNetworkAliases(postgresNetworkAlias)
        .withNetwork(network)
        .withExposedPorts(5432)
        .waitingFor(HostPortWaitStrategy())
        .apply {
            start()
        }
}
