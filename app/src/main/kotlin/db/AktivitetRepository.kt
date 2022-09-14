package db

import domene.Aktivitetsmal
import domene.ArbeidsgiverRepresentant
import domene.ValgtAktivitet
import domene.ValgtAktivitet.Companion.velgAktivitet
import domene.Virksomhet
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.util.UUID

private object ValgtAktivitetTabell : IntIdTable(name = "valgtaktivitet") {
    val aktivitetsmalID = uuid(name = "aktivitetsmal_id")
    val virksomhetsnummer = varchar(name = "virksomhetsnummer", length = 20)
    val fødselsnummer = varchar(name = "fødselsnummer", 11)
    val fullført = bool("fullfoert")
    val fullførtTidspunkt = timestamp("fullfoert_tidspunkt")
    val opprettelsesTidspunkt = timestamp("opprettelsestidspunkt")

    fun tilValgtAktivitet(it: ResultRow) =
        ArbeidsgiverRepresentant(fnr = it[fødselsnummer], virksomhet = Virksomhet(orgnr = it[virksomhetsnummer]))
            .velgAktivitet(
                aktivitetsmal = Aktivitetsmal(id = it[aktivitetsmalID]),
                id = it[id].value,
                fullført = it[fullført],
                fullførtTidspunkt = it[fullførtTidspunkt],
                opprettelsesTidspunkt = it[opprettelsesTidspunkt]
            )
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

    fun lagreValgtAktivitet(valgtAktivitet: ValgtAktivitet) = transaction {
        ValgtAktivitetTabell.tilValgtAktivitet(ValgtAktivitetTabell.insert {
            it[aktivitetsmalID] = valgtAktivitet.aktivitetsmal.id
            it[virksomhetsnummer] = valgtAktivitet.valgtAv.virksomhet.orgnr
            it[fødselsnummer] = valgtAktivitet.valgtAv.fnr
            it[fullført] = valgtAktivitet.fullført
            if (valgtAktivitet.fullført) {
                it[fullførtTidspunkt] = Instant.now()
            }
        }.resultedValues!!.first())
    }

    fun fullfør(aktivitetId: Int, orgnr: String) {
        transaction {
            ValgtAktivitetTabell
                .update(where = {
                    ValgtAktivitetTabell.id eq aktivitetId and (ValgtAktivitetTabell.virksomhetsnummer eq orgnr)
                }) {
                    it[fullført] = true
                    it[fullførtTidspunkt] = Instant.now()
                }
        }
    }

    fun hentFullførteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        hentValgteAktiviteterForVirksomhet(virksomhet).filter { it.fullført }

    // MOCK DATA INNTIL VI HAR DB PÅ PLASS
    private var aktivitetsmaler: List<Aktivitetsmal> = listOf(
        Aktivitetsmal(
            id = UUID.fromString("93cea6df-6261-4e28-9ad2-9915cc3e6097"),
            tittel = "Pilotering av medarbeidersamtalen"
        ),
        Aktivitetsmal(
            id = UUID.fromString("86b3ce04-7ff2-41a1-ab99-9493fd4afd62"),
            tittel = "Kartleggingsmøte med ansatt"
        ),
        Aktivitetsmal(
            id = UUID.fromString("3ccee238-a5b6-41b1-98af-fb2243718e3e"),
            tittel = "Sinnemestring"
        ),
        Aktivitetsmal(
            id = UUID.fromString("3ccee238-a5b6-41b1-98af-fb2243718e3e"),
            tittel = "Hvordan ta den vanskelige praten?"
        ),
    )
}
