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
    private val hashetFodselsnummer = byteArrayOf(1, 2, 3)
    private val orgnr = "123"
    private val aktivitet = Aktivitet(
        hashetFodselsnummer = hashetFodselsnummer,
        orgnr = orgnr,
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

    @Test
    fun `hent alle fullførte aktiviteter burde hente alle aktiviteter for hashetFodselsnummer og orgnr`() {
        val aktivitet2 = aktivitet.copy(aktivitetsid = "aktivitetsid2")
        val aktivitetSomIkkeErFullført = aktivitet.copy(aktivitetsid = "ikkeFullfort", fullført = false)
        val aktivitetMedAnnetOrgnr = aktivitet.copy(orgnr = "9999")
        AktiviteterRepository.settAktivitet(aktivitet)
        AktiviteterRepository.settAktivitet(aktivitet2)
        AktiviteterRepository.settAktivitet(aktivitetSomIkkeErFullført)
        AktiviteterRepository.settAktivitet(aktivitetMedAnnetOrgnr)

        val resultat = AktiviteterRepository.hentAlleFullførteAktiviteterFor(hashetFodselsnummer, orgnr)

        resultat shouldContainExactly listOf(aktivitet, aktivitet2)
    }

    private fun AktiviteterRepository.hentAlleAktiviteter(): List<Aktivitet> = transaction {
        selectAll().map(::tilDomene)
    }
}