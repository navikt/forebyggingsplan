package container.auth

import container.helper.TestContainerHelper
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import request.AktivitetApi
import kotlin.test.Test

internal class AuthenticationTest {

    private val aktivitetApi = AktivitetApi(TestContainerHelper.forebyggingsplanContainer)

    @Test
    fun `happy path - skal få 200 ok ved henting av aktivitet`() {
        aktivitetApi.hentAktiviteter().second.statusCode shouldBe HttpStatusCode.OK.value
    }
}