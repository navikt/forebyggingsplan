package db

import domene.Aktivitetsmal
import domene.ArbeidsgiverRepresentant
import domene.FullførtAktivitet
import domene.ValgtAktivitet
import domene.ValgtAktivitet.Companion.velgAktivitet
import domene.Virksomhet
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

private object ValgtAktivitetTabell : IntIdTable(name = "valgtaktivitet") {
    val uuid = uuid(name = "uuid")
    val virksomhetsnummer = varchar(name = "virksomhetsnummer", length = 20)
    val fødselsnummer = varchar(name = "fødselsnummer", 11)

    fun tilValgtAktivitet(it: ResultRow) =
        ArbeidsgiverRepresentant(it[fødselsnummer], virksomhet = Virksomhet(orgnr = it[virksomhetsnummer]))
            .velgAktivitet(Aktivitetsmal(id = it[uuid], tittel = ""))
}

class AktivitetRepository {

    fun hentAktivitetsmaler() = aktivitetsmaler

    fun hentAktivitetsmal(aktivitetsmalId: String) = aktivitetsmaler.find { it.id == UUID.fromString(aktivitetsmalId) }

    fun hentValgteAktiviteterForVirksomhet(virksomhet: Virksomhet): List<ValgtAktivitet> =
        transaction {
            ValgtAktivitetTabell.select {
                ValgtAktivitetTabell.virksomhetsnummer eq virksomhet.orgnr
            }.map(ValgtAktivitetTabell::tilValgtAktivitet)
        }

    fun lagreValgtAktivitet(valgtAktivitet: ValgtAktivitet): ValgtAktivitet {
        transaction {
            ValgtAktivitetTabell.insert {
                it[uuid] = valgtAktivitet.aktivitetsmal.id
                it[virksomhetsnummer] = valgtAktivitet.valgtAv.virksomhet.orgnr
                it[fødselsnummer] = valgtAktivitet.valgtAv.fnr
            }
        }

        return valgtAktivitet
    }

    fun hentFullførteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        fullførteAktiviteter.filter { it.fullførtAv.virksomhet == virksomhet }

    // MOCK DATA INNTIL VI HAR DB PÅ PLASS
    private var aktivitetsmaler: List<Aktivitetsmal> = listOf(
        Aktivitetsmal(
            tittel = "Pilotering av medarbeidersamtalen"
        ),
        Aktivitetsmal(
            tittel = "Kartleggingsmøte med ansatt"
        ),
        Aktivitetsmal(
            tittel = "Sinnemestring"
        ),
        Aktivitetsmal(
            tittel = "Hvordan ta den vanskelige praten?"
        ),
    )

    private var fullførteAktiviteter: MutableList<FullførtAktivitet> = mutableListOf()
    // SLUTT MOCK DATA

}
