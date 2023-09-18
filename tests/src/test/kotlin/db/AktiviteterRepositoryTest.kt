package db

import container.helper.PostgresContainer
import domene.Aktivitet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class AktiviteterRepositoryKotest : FunSpec({
    extension(PostgresContainer())

    val hashetFodselsnummer = byteArrayOf(1, 2, 3)
    val orgnr = "123"
    val aktivitet = Aktivitet.Aktivitetskort(
        hashetFodselsnummer = hashetFodselsnummer,
        orgnr = orgnr,
        aktivitetsid = "aktivitetsid",
        aktivitetsversjon = "aktivitetsversjon",
        fullført = true,
        fullføringstidspunkt = LocalDateTime(2023, 1, 1, 0, 0, 0).toInstant(TimeZone.UTC)
    )

    fun SqlAktiviteterRepository.hentAlleAktiviteter(): List<Aktivitet.Aktivitetskort> = transaction {
        selectAll().map(::tilDomene)
    }

    test("sett aktivitet burde skrive ny aktivitet til db") {
        SqlAktiviteterRepository.settAktivitet(aktivitet)

        val alleAktiviteter = SqlAktiviteterRepository.hentAlleAktiviteter()

        alleAktiviteter shouldContainExactly listOf(aktivitet)
    }

    test("sett aktivitet burde oppdatere eksisterende aktivitet") {
        val oppdatertAktivitet = aktivitet.copy(fullført = false)

        SqlAktiviteterRepository.settAktivitet(aktivitet)
        SqlAktiviteterRepository.settAktivitet(oppdatertAktivitet)

        val alleAktiviteter = SqlAktiviteterRepository.hentAlleAktiviteter()

        alleAktiviteter shouldContainExactly listOf(oppdatertAktivitet)
    }

    test("hent alle fullførte aktiviteter burde hente alle aktiviteter for hashet fødselsnummer og orgnr") {
        val aktivitet2 = aktivitet.copy(aktivitetsid = "aktivitetsid2")
        val aktivitetSomIkkeErFullført =
            aktivitet.copy(aktivitetsid = "ikkeFullfort", fullført = false)
        val aktivitetMedAnnetOrgnr = aktivitet.copy(orgnr = "9999")
        SqlAktiviteterRepository.settAktivitet(aktivitet)
        SqlAktiviteterRepository.settAktivitet(aktivitet2)
        SqlAktiviteterRepository.settAktivitet(aktivitetSomIkkeErFullført)
        SqlAktiviteterRepository.settAktivitet(aktivitetMedAnnetOrgnr)

        val resultat =
            SqlAktiviteterRepository.hentAlleFullførteAktiviteterFor(hashetFodselsnummer, orgnr)

        resultat shouldContainExactly listOf(aktivitet, aktivitet2)
    }

})
