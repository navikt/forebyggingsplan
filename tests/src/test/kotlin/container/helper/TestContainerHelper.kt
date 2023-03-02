package container.helper

import api.sanity.SanityForebyggingsplan
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.Testcontainers
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.images.builder.ImageFromDockerfile
import request.TestHttpClient
import wiremock.com.google.common.net.HttpHeaders.CONTENT_TYPE
import kotlin.io.path.Path

internal class TestContainerHelper {
    internal companion object {
        private val network = Network.newNetwork()
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
        private val authServer = AuthContainer(network = network)
        private val database = PostgresContainer(network = network)
        private const val serviceCode = "5934"
        private const val serviceEdition = "1"

        private val wireMock = WireMockServer(WireMockConfiguration.options().dynamicPort()).also {
            it.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/altinn/v2/organisasjoner"))
                    .willReturn(
                        WireMock.ok()
                            .withHeader(CONTENT_TYPE, "application/json")
                            .withBody(
                                """[
                                {
                                    "Name": "BALLSTAD OG HAMARØY",
                                     "Type": "Business",
                                     "OrganizationNumber": "811076732",
                                     "ParentOrganizationNumber": "811076112",
                                     "OrganizationForm": "BEDR",
                                     "Status": "Active"
                                }, 
                                {
                                    "Name": "FIKTIVIA",
                                     "Type": "Business",
                                     "OrganizationNumber": "315829062",
                                     "ParentOrganizationNumber": "811076112",
                                     "OrganizationForm": "BEDR",
                                     "Status": "Active"
                                }
                            ]""".trimMargin()
                            )
                    )
            )
            it.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/altinn/v2/organisasjoner"))
                    .withQueryParam("serviceCode", equalTo(serviceCode))
                    .withQueryParam("serviceEdition", equalTo(serviceEdition))
                    .willReturn(
                        WireMock.ok()
                            .withHeader(CONTENT_TYPE, "application/json")
                            .withBody(
                                """[
                                {
                                    "Name": "BALLSTAD OG HAMARØY",
                                     "Type": "Business",
                                     "OrganizationNumber": "811076732",
                                     "ParentOrganizationNumber": "811076112",
                                     "OrganizationForm": "BEDR",
                                     "Status": "Active"
                                }
                            ]""".trimMargin()
                            )
                    )
            )
            it.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/v2022-10-28/data/query/${SanityForebyggingsplan.Dataset.Development.name.lowercase()}"))
                    .willReturn(
                        WireMock.ok()
                            .withHeader(CONTENT_TYPE, "application/json")
                            .withBody(
                                """{
                                    "ms": 710,
                                    "query": "*[_type == 'Aktivitet' \u0026\u0026 _id == 'f9daa4cb-a432-4945-8436-6f7d3fa32a5d']",
                                    "result": [
                                        {
                                            "_createdAt": "2022-11-30T09:31:05Z",
                                            "_id": "f9daa4cb-a432-4945-8436-6f7d3fa32a5d",
                                            "_rev": "3C549LRrvVH6gsnZfS148M",
                                            "_type": "Aktivitet",
                                            "_updatedAt": "2022-12-05T08:58:14Z",
                                            "beskrivelse": "Første aktivitet har en beskrivelse",
                                            "embeddedInnhold": [
                                                {
                                                    "_key": "e3d11e24ce68",
                                                    "_type": "block",
                                                    "children": [
                                                        {
                                                            "_key": "5ba6295a4490",
                                                            "_type": "span",
                                                            "marks": [],
                                                            "text": "Hei, her er det noe tekst"
                                                        }
                                                    ],
                                                    "markDefs": [],
                                                    "style": "normal"
                                                },
                                                {
                                                    "_key": "975c816813e3",
                                                    "_type": "block",
                                                    "children": [
                                                        {
                                                            "_key": "4c0cf96bedfb",
                                                            "_type": "span",
                                                            "marks": [],
                                                            "text": ""
                                                        }
                                                    ],
                                                    "markDefs": [],
                                                    "style": "normal"
                                                },
                                                {
                                                    "_key": "174b82d2c890",
                                                    "_type": "seksjon",
                                                    "seksjonInnhold": []
                                                }
                                            ],
                                            "kategori": {
                                                "_ref": "d1e19bae-4625-437f-8bba-4ec5d7825272",
                                                "_type": "reference"
                                            },
                                            "maal": "Og et relativt enkelt mål",
                                            "tittel": "Første aktivitet"
                                        }
                                    ]
                                }
                                """
                            )
                    )
            )

            if (!it.isRunning) {
                it.start()
            }

            println("Starter Wiremock på port ${it.port()}")
            Testcontainers.exposeHostPorts(it.port())
        }

        internal val forebyggingsplanContainer: GenericContainer<*> =
            GenericContainer(
                ImageFromDockerfile()
                    .withDockerfile(Path("../Dockerfile"))
            )
                .withNetwork(network)
                .dependsOn(authServer.container, database.container)
                .withEnv(
                    mapOf(
                        "DB_HOST" to database.postgresNetworkAlias,
                        "DB_DATABASE" to database.dbName,
                        "DB_PORT" to "5432",
                        "DB_USERNAME" to database.container.username,
                        "DB_PASSWORD" to database.container.password,
                        "TOKEN_X_CLIENT_ID" to "hei",
                        "TOKEN_X_ISSUER" to "http://authserver:6969/default",
                        "TOKEN_X_JWKS_URI" to "http://authserver:6969/default/jwks",
                        "TOKEN_X_TOKEN_ENDPOINT" to "http://authserver:6969/default/token",
                        "TOKEN_X_PRIVATE_JWK" to """{
                                                    "p": "1sKc9CQFXJ5q14wGjk6bAhIaWBiM2ZJHNCLcME0P60q_dNaC7osoj0-zDTwUWdiREIiI2y3DAArAGNlhyZqZwDNumL08_pM-ePXVoqiZWZ87Ch8g8csx27yU_AsDj6h64qRpV07x_TOzXRJdP5iQm_IO3qjyul9qlnXyd2X9h3c",
                                                    "kty": "RSA",
                                                    "q": "xkS_rKKUfowRYmHfha4birMJMvrRZEBmvOPs9gerRUyIy32R36UT5f2B8xwycExivtZpnlz-YgBrglIpWWXX1gUtgLb4dV_YQNE4rABQjWoa62NJeCeaL5mOoVJ-6Xx2mgt9Tb9JdZVyfQuC9-s74ImgKyYaN8y7LcW7EqxNa60",
                                                    "d": "TUr875CxdUBnuufXfGe9WELPlLE2N4tVtHO85qrVuwn41CueKKk92bF6mK4fFF_oIP6Ja22B96i7d-AY5GtLcwIJA_HNy6ndYJCWiMX9GlDJ7Y2TyYXrk4YXpZQWI3x18X7wbDs0JX1eVsxs2VWhjzyEsJfEbp0cyagBIZR_GE_WecEahhBUV2eGl9qf0qL50MnckFOZhQErEpyr0XPTfjqktwpmjZkTdONyvKoJhXhm7bngFQHl63RX3fIElsYFsvMYNpAH_I5NZg76Va79txrfR7X0diG6XZ4Kc5iUXXL1ZFnqgijVOzUYfldDikxaXc5wKPL5Jbs2GBe1fB14eQ",
                                                    "e": "AQAB",
                                                    "use": "sig",
                                                    "kid": "tokenx",
                                                    "qi": "zNeG8JxnjxSlCWbRv2sHwld6tf1OtDKTimo4VbNdOqmrm8sSUkuM9h0mrH0ZUbC7Q1n0Cp-4T_Q82QVzKXX71bGSolTI7c6NCTnzQXgTEylMaHgv-9MIG1N4raxWemlOt_0ZgdTjwDWNPXfbbx0oyc4NBJVZpQH_KEXKirAY5aI",
                                                    "dp": "Pbe8B2V6rP1R0xQIpkjsvxGYxIx5neUt1UvXX4Il-waGMvuasRcI1vaejEUhzBgyyD-UpPhnu9FbF0kRkzB80wF03Sw1JSwHnhd4B8DQITNjcisz-ojckTuGzVAU--n9NrjtFQw4-v0qpKqsZaRgmpBbuZ1v9COLrCXFQo7q500",
                                                    "alg": "RS256",
                                                    "dq": "Ccu_xKHLwGzfNwMq7gnqJnIuFCy8R72-1bpVLNq4JZZgc91iZbBcSVK7Ju3PuCiuAEvLsB1cHC91IF062cXkYhijZOalY_c2Ug2ERUtGr5X8eoDPUnZyccOefm37A0I5Aedra3n2AS8_FtqIwAMJVFC4bylUxkkBPoO0eHm24Yk",
                                                    "n": "plQx4or1C_Xany-wjM7mPHB4CAJPk3oOEdDSKpTwJ2dzGji5tEq7dUxExyhFN8f0PUjBjXyPph0gmDWaJG64fnhSSwVI-8Tdf2PppuK4rdCtWSPLgZ_DJ2DruxHgeXgwvJnX1HRfqhJF2p4ClkRUiVXZKFOhRPMGVgg18fnV9fXz5C4JacP_fmh498ktEohwcL3Pbv5DI_po_i0OiyF_M-9Iic3Ss80j22hs1wsNBGEMHvofWs7sl3ufwxmUCIstnDNSat840-n21Q4GV2v4L2kpROUw6l4ZmqZxoGl7eRSDS_VC5rPQoQEZYfyCiq6o1W5p9UXnoQin1zn0lr5Iaw"
                                                }""".trimIndent(),
                        "ALTINN_RETTIGHETER_PROXY_CLIENT_ID" to "hei",
                        "TZ" to "Europe/Oslo",
                        "JAVA_TOOL_OPTIONS" to "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
                        "ALTINN_RETTIGHETER_PROXY_URL" to "http://host.testcontainers.internal:${wireMock.port()}/altinn",
                        "SANITY_HOST" to "http://host.testcontainers.internal:${wireMock.port()}",
                        "NAIS_CLUSTER_NAME" to "local",
                        "ROOT_LOG_APPENDER" to "STDOUT"
                    )
                )
                .withExposedPorts(8080, 5005)
                .withLogConsumer(
                    Slf4jLogConsumer(log).withPrefix("forebyggingsplanContainer")
                        .withSeparateOutputStreams()
                )
                .withCreateContainerCmdModifier { cmd -> cmd.withName("forebyggingsplan-${System.currentTimeMillis()}") }
                .waitingFor(HttpWaitStrategy().forPort(8080).forPath("/internal/isReady")).apply {
                    start()
                }

        private fun GenericContainer<*>.buildUrl(url: String) = "http://${this.host}:${this.getMappedPort(8080)}/$url"

        infix fun GenericContainer<*>.shouldContainLog(regex: Regex) = logs shouldContain regex

        private suspend fun GenericContainer<*>.performRequest(
            url: String,
            block: HttpRequestBuilder.() -> Unit = {},
        ): HttpResponse {
            val urlToCall = buildUrl(url = url)
            return TestHttpClient.client.request(urlString = urlToCall, block = block)
        }

        internal suspend fun GenericContainer<*>.performGet(url: String, block: HttpRequestBuilder.() -> Unit = {}) =
            performRequest(url = url) {
                apply(block)
                method = HttpMethod.Get
            }

        internal suspend fun GenericContainer<*>.performPost(url: String, block: HttpRequestBuilder.() -> Unit = {}) =
            performRequest(url) {
                apply(block)
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
            }

        internal fun accessToken(
            subject: String = "123",
            audience: String = "hei",
            claims: Map<String, String> = mapOf(
                "acr" to "Level4",
                "pid" to subject
            ),
        ) = authServer.issueToken(
            subject = subject,
            audience = audience,
            claims = claims
        )
    }
}
