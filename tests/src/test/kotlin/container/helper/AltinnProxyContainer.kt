package container.helper

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.utility.DockerImageName

internal class AltinnProxyContainer(network: Network = Network.newNetwork()) {
    private val port = 9090
    private val networkAlias = "altinnproxy"

    internal val container =
        GenericContainer(DockerImageName.parse("ghcr.io/navikt/altinn-rettigheter-proxy/altinn-rettigheter-proxy:latest"))
        .withNetwork(network)
        .withNetworkAliases(networkAlias)
        .withExposedPorts(port)
        .withEnv(
            mapOf(
                "SERVER_PORT" to port.toString(),
                "TZ" to "Europe/Oslo",
                "LOGINSERVICE_IDPORTEN_DISCOVERY_URL" to "http://authserver:6969/loginservice/.well-known/openid-configuration",
                "LOGINSERVICE_IDPORTEN_AUDIENCE" to "someaudience",
                "TOKEN_X_WELL_KNOWN_URL" to "http://authserver:6969/tokenx/.well-known/openid-configuration",
                "TOKEN_X_CLIENT_ID" to "someaudience",
                "SPRING_PROFILES_ACTIVE" to "local",
                "ALTINN_URL" to "http://localhost:9091/altinn"
            )
        ).waitingFor(HttpWaitStrategy().forPort(9090).forPath("/internal/healthcheck")).apply {
            start()
        }

    fun baseUrl() = "http://$networkAlias:$port/"
}
