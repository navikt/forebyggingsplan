package container.helper

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.images.builder.ImageFromDockerfile
import kotlin.io.path.Path

internal class TestContainerHelper {
    internal companion object {
        private val network = Network.newNetwork()
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
        internal val authServer = AuthContainer(network = network)
        internal val forebyggingsplanContainer: GenericContainer<*> =
            GenericContainer(ImageFromDockerfile()
                .withDockerfile(Path("../Dockerfile")))
                .withNetwork(network)
                .dependsOn(authServer.container)
                .withEnv(mapOf(
                    "TOKEN_X_CLIENT_ID" to "hei",
                    "TOKEN_X_ISSUER" to "tokenx",
                    "TZ" to "Europe/Oslo"
                ))
                .withExposedPorts(8080)
                .withLogConsumer(Slf4jLogConsumer(log).withPrefix("forebyggingsplanContainer").withSeparateOutputStreams())
                .withCreateContainerCmdModifier { cmd -> cmd.withName("forebyggingsplan-${System.currentTimeMillis()}") }
                .waitingFor(HttpWaitStrategy().forPath("/internal/isReady")).apply {
                    start()
                }

        private fun GenericContainer<*>.buildUrl(url: String) = "http://${this.host}:${this.getMappedPort(8080)}/$url"
        fun GenericContainer<*>.performGet(url: String) = buildUrl(url = url).httpGet()
        fun GenericContainer<*>.performPost(url: String) = buildUrl(url = url).httpPost()

        internal fun accessToken(
            subject: String = "123",
            audience: String = "hei",
            claims: Map<String, String> = mapOf(
                "acr" to "Level4"
            )
        ) = authServer.issueToken(
            subject = subject,
            audience = audience,
            claims = claims
        )
    }
}
