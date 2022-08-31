package container.helper

import org.testcontainers.containers.*
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.utility.DockerImageName

internal class AltinnProxyMockContainer(network: Network = Network.newNetwork()) {
    private val port = 9090
    private val networkAlias = "altinnproxy"

    internal val container =
        MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver").withTag("mockserver-5.5.4"))
            .withNetwork(network)
            .withNetworkAliases(networkAlias)
            .withExposedPorts(port)
            .withEnv(
                mapOf(
                    "SERVER_PORT" to port.toString(),
                    "TZ" to "Europe/Oslo",
                )
            ).apply {
                start()
            }


    fun baseUrl() = "http://$networkAlias:$port/"
}
