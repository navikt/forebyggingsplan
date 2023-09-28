package container

import api.dto.ValgtAktivitetDTO
import container.helper.TestContainerHelper
import container.helper.withToken
import enVirksomhet
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.toKotlinLocalDate
import request.ForebyggingsplanApi
import java.time.LocalDate
import java.util.*
import kotlin.test.Test

class AktivitetContainerTest {
    private val forebyggingsplanApi = ForebyggingsplanApi(TestContainerHelper.forebyggingsplanContainer)

}