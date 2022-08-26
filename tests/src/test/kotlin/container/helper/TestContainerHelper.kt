package container.helper

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.images.builder.ImageFromDockerfile
import request.TestHttpClient
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
                    "TOKEN_X_ISSUER" to "http://authserver:6969/default",
                    "TOKEN_X_JWKS_URI" to "http://authserver:6969/default/jwks",
                    "TZ" to "Europe/Oslo",
                    "JAVA_TOOL_OPTIONS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
                ))
                .withExposedPorts(8080, 5005)
                .withLogConsumer(Slf4jLogConsumer(log).withPrefix("forebyggingsplanContainer").withSeparateOutputStreams())
                .withCreateContainerCmdModifier { cmd -> cmd.withName("forebyggingsplan-${System.currentTimeMillis()}") }
                .waitingFor(HttpWaitStrategy().forPort(8080).forPath("/internal/isReady")).apply {
                    start()
                }

        private fun GenericContainer<*>.buildUrl(url: String) = "http://${this.host}:${this.getMappedPort(8080)}/$url"

        private suspend fun GenericContainer<*>.performRequest(url: String, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
            val urlToCall = buildUrl(url = url)
            return TestHttpClient.client.request(urlString = urlToCall, block = block)
        }
        internal suspend fun GenericContainer<*>.performGet(url: String, block: HttpRequestBuilder.() -> Unit = {}) = performRequest(url = url) {
            apply(block)
            method = HttpMethod.Get
        }
        internal suspend fun GenericContainer<*>.performPost(url: String, block: HttpRequestBuilder.() -> Unit = {}) = performRequest(url) {
            apply(block)
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
        }

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
