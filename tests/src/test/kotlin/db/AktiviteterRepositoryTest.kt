package db

import container.helper.PostgresContainer
import domene.Aktivitet
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(PostgresContainer::class)
class AktiviteterRepositoryTest {
    private val aktivitet = Aktivitet(
        hashetFodselsnummer = byteArrayOf(1, 2, 3),
        orgnr = "123",
        aktivitetsid = "aktivitetsid",
        aktivitetsversjon = "aktivitetsversjon",
        fullført = true,
        fullføringstidspunkt = LocalDateTime(2023, 1, 1, 0, 0, 0).toInstant(TimeZone.UTC)
    )

    @Test
    fun `set aktivitet burde skrive ny aktivitet til db`() {
        AktiviteterRepository.settAktivitet(aktivitet)

        val alleAktiviteter = AktiviteterRepository.hentAlleAktiviteter()

        alleAktiviteter shouldContainExactly listOf(aktivitet)
    }

    @Test
    fun `set aktivitet burde oppdatere eksisterende aktivitet`() {
        val oppdatertAktivitet = aktivitet.copy(fullført = false)

        AktiviteterRepository.settAktivitet(aktivitet)
        AktiviteterRepository.settAktivitet(oppdatertAktivitet)

        val alleAktiviteter = AktiviteterRepository.hentAlleAktiviteter()

        alleAktiviteter shouldContainExactly listOf(oppdatertAktivitet)
    }

    private fun AktiviteterRepository.hentAlleAktiviteter(): List<Aktivitet> = transaction {
        selectAll().map(::tilDomene)
    }
}