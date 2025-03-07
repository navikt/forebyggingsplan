package api

import api.endepunkt.json.AktivitetJson
import api.endepunkt.json.Aktivitetstype
import api.endepunkt.json.OppdaterAktivitetJson
import container.helper.TestContainerHelper
import container.helper.TestContainerHelper.Companion.performGet
import container.helper.TestContainerHelper.Companion.performPost
import container.helper.enVirksomhet
import container.helper.withToken
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

class HttpGetAktiviteterTest :
    FunSpec({
        extension(TestContainerHelper.database)

        val autorisertOrgnr = enVirksomhet.orgnr

        context("hent aktiviteter") {
            test("svarer med 401 hvis ikke vi er logget inn") {
                val resultat =
                    TestContainerHelper.forebyggingsplanContainer.performGet("/aktiviteter/orgnr/123456789")

                resultat.status shouldBe HttpStatusCode.Unauthorized
            }

            test("svarer med 200 OK med tom JSON-array hvis brukeren ikke har noen aktiviteter") {
                val resultat = TestContainerHelper.forebyggingsplanContainer.performGet(
                    "/aktiviteter/orgnr/$autorisertOrgnr",
                    withToken(),
                )

                resultat.bodyAsText() shouldBe "[]"
                resultat.status shouldBe HttpStatusCode.OK
            }

            test("svarer med 200 OK med oppgaver") {
                val aktivitetId = "123"
                val aktivitetJson = AktivitetJson(
                    aktivitetId = aktivitetId,
                    aktivitetType = Aktivitetstype.OPPGAVE,
                    status = AktivitetJson.Status.STARTET,
                )
                TestContainerHelper.forebyggingsplanContainer.performPost(
                    "/aktivitet/$aktivitetId/orgnr/$autorisertOrgnr/oppdater",
                    withToken {
                        setBody(
                            OppdaterAktivitetJson(
                                aktivitetstype = null,
                                status = "STARTET",
                            ),
                        )
                    },
                )
                val resultat = TestContainerHelper.forebyggingsplanContainer.performGet(
                    "/aktiviteter/orgnr/$autorisertOrgnr",
                    withToken(),
                )

                resultat.status shouldBe HttpStatusCode.OK

                val body = resultat.body<List<AktivitetJson>>()
                body shouldContainExactly listOf(aktivitetJson)
            }

            test("svarer med 200 OK med teoriseksjoner") {
                val aktivitetId = "123"
                val aktivitetJson = AktivitetJson(
                    aktivitetId = aktivitetId,
                    aktivitetType = Aktivitetstype.TEORISEKSJON,
                    status = AktivitetJson.Status.LEST,
                )
                TestContainerHelper.forebyggingsplanContainer.performPost(
                    "/aktivitet/$aktivitetId/orgnr/$autorisertOrgnr/oppdater",
                    withToken {
                        setBody(
                            OppdaterAktivitetJson(
                                aktivitetstype = Aktivitetstype.TEORISEKSJON,
                                status = "LEST",
                            ),
                        )
                    },
                )
                val resultat = TestContainerHelper.forebyggingsplanContainer.performGet(
                    "/aktiviteter/orgnr/$autorisertOrgnr",
                    withToken(),
                )

                resultat.status shouldBe HttpStatusCode.OK

                val body = resultat.body<List<AktivitetJson>>()
                body shouldContainExactly listOf(aktivitetJson)
            }

            test("svarer med 200 OK med alle typer aktiviteter") {
                val teoriseksjonId = "123"
                val oppgaveId = "456"
                val teoriseksjon = AktivitetJson(
                    aktivitetId = teoriseksjonId,
                    aktivitetType = Aktivitetstype.TEORISEKSJON,
                    status = AktivitetJson.Status.LEST,
                )
                val oppgave = AktivitetJson(
                    aktivitetId = oppgaveId,
                    aktivitetType = Aktivitetstype.OPPGAVE,
                    status = AktivitetJson.Status.FULLFØRT,
                )
                TestContainerHelper.forebyggingsplanContainer.performPost(
                    "/aktivitet/$teoriseksjonId/orgnr/$autorisertOrgnr/oppdater",
                    withToken {
                        setBody(
                            OppdaterAktivitetJson(
                                aktivitetstype = Aktivitetstype.TEORISEKSJON,
                                status = "LEST",
                            ),
                        )
                    },
                )
                TestContainerHelper.forebyggingsplanContainer.performPost(
                    "/aktivitet/$oppgaveId/orgnr/$autorisertOrgnr/oppdater",
                    withToken {
                        setBody(
                            OppdaterAktivitetJson(
                                aktivitetstype = Aktivitetstype.OPPGAVE,
                                status = "FULLFØRT",
                            ),
                        )
                    },
                )
                val resultat = TestContainerHelper.forebyggingsplanContainer.performGet(
                    "/aktiviteter/orgnr/$autorisertOrgnr",
                    withToken(),
                )

                resultat.status shouldBe HttpStatusCode.OK

                val body = resultat.body<List<AktivitetJson>>()
                body shouldContainExactly listOf(teoriseksjon, oppgave)
            }
        }
    })
