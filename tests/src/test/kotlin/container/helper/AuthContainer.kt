package container.helper

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

internal object AuthContainer {
    internal val server = GenericContainer(DockerImageName.parse("ghcr.io/navikt/mock-oauth2-server:0.5.1"))
        .withExposedPorts(6969)
        .withEnv(
            mapOf(
                "SERVER_PORT" to "6969",
                "TZ" to "Europe/Oslo"
            )
        )
        .waitingFor(Wait.forHttp("/default/.well-known/openid-configuration").forStatusCode(200))
        .apply { start() }
}