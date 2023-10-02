package db

import domene.Aktivitet

interface AktiviteterRepository {
    fun oppdaterOppgave(oppgave: Aktivitet.Oppgave)
    fun hentAktiviteter(hashetFnr: ByteArray, orgnr: String): List<Aktivitet>
}