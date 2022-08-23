package container.auth

import container.helper.TestContainerHelper
import container.helper.withToken
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import request.AktivitetApi
import kotlin.test.Test

internal class AuthenticationTest {

    private val aktivitetApi = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    fun `happy path - skal få 200 ok ved henting av aktivitet`() {
        runBlocking {
            aktivitetApi.hentAktiviteter(withToken()).status shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun `skal få 401 unauthorized på kall uten token`() {
        runBlocking {
            aktivitetApi.hentAktiviteter().status shouldBe HttpStatusCode.Unauthorized
        }
    }
}