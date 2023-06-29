package db

import domene.Aktivitetsmal
import domene.ArbeidsgiverRepresentant
import domene.ValgtAktivitet
import domene.ValgtAktivitet.Companion.velgAktivitet
import domene.Virksomhet
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

private object ValgtAktivitetTabell : IntIdTable(name = "valgtaktivitet") {
    val aktivitetsmalID = varchar(name = "aktivitetsmal_id", length = 45)
    val aktivitetsversjon = varchar(name = "aktivitetsversjon", length = 45)
    val frist = date("frist").nullable()
    val virksomhetsnummer = varchar(name = "virksomhetsnummer", length = 20)
    val fullført = bool("fullfort")
    val fullførtTidspunkt = timestamp("fullfort_tidspunkt")
    val opprettelsesTidspunkt = timestamp("opprettelsestidspunkt")

    fun tilValgtAktivitet(it: ResultRow) =
        ArbeidsgiverRepresentant(virksomhet = Virksomhet(orgnr = it[virksomhetsnummer]))
            .velgAktivitet(
                aktivitetsmal = Aktivitetsmal(id = it[aktivitetsmalID], versjon = it[aktivitetsversjon]),
                frist = it[frist]?.toKotlinLocalDate(),
                id = it[id].value,
                fullført = it[fullført],
                fullførtTidspunkt = it[fullførtTidspunkt],
                opprettelsesTidspunkt = it[opprettelsesTidspunkt]
            )
}

@Deprecated("Bruk personlig aktivitetsrepository")
class ValgtAktivitetRepository {
    fun hentValgteAktiviteterForVirksomhet(virksomhet: Virksomhet): List<ValgtAktivitet> =
        transaction {
            ValgtAktivitetTabell.select {
                ValgtAktivitetTabell.virksomhetsnummer eq virksomhet.orgnr
            }.map(ValgtAktivitetTabell::tilValgtAktivitet)
        }

    fun lagreValgtAktivitet(valgtAktivitet: ValgtAktivitet) = transaction {
        ValgtAktivitetTabell.tilValgtAktivitet(ValgtAktivitetTabell.insert {
            it[aktivitetsmalID] = valgtAktivitet.aktivitetsmal.id
            it[aktivitetsversjon] = valgtAktivitet.aktivitetsmal.versjon
            it[frist] = valgtAktivitet.frist?.toJavaLocalDate()
            it[virksomhetsnummer] = valgtAktivitet.valgtAv.virksomhet.orgnr
            it[fullført] = valgtAktivitet.fullført
            if (valgtAktivitet.fullført) {
                it[fullførtTidspunkt] = Instant.now()
            }
        }.resultedValues!!.first())
    }

    fun fullfør(valgtAktivitet: ValgtAktivitet) {
        transaction {
            ValgtAktivitetTabell
                .update(where = {
                    ValgtAktivitetTabell.id eq valgtAktivitet.id and (ValgtAktivitetTabell.virksomhetsnummer eq valgtAktivitet.valgtAv.virksomhet.orgnr)
                }) {
                    it[fullført] = true
                    it[fullførtTidspunkt] = Instant.now()
                }
        }
    }

    fun endreFrist(valgtAktivitet: ValgtAktivitet, nyFrist: LocalDate?) {
        transaction {
            ValgtAktivitetTabell
                .update(where = {
                    ValgtAktivitetTabell.id eq valgtAktivitet.id and (ValgtAktivitetTabell.virksomhetsnummer eq valgtAktivitet.valgtAv.virksomhet.orgnr)
                }) {
                    it[frist] = nyFrist?.toJavaLocalDate()
                }
        }
    }

    fun hentValgtAktivitet(virksomhet: Virksomhet, aktivitetsId: Int): ValgtAktivitet =
        transaction {
            ValgtAktivitetTabell.select {
                ValgtAktivitetTabell.virksomhetsnummer eq virksomhet.orgnr and (ValgtAktivitetTabell.id eq aktivitetsId)
            }.map(ValgtAktivitetTabell::tilValgtAktivitet).single()
        }
}
