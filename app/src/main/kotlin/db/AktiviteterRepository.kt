package db

import domene.Aktivitet

interface AktiviteterRepository {
    fun settAktivitet(aktivitet: Aktivitet)
    fun hentAlleFullførteAktiviteterFor(
        hashetFnr: ByteArray,
        orgnr: String
    ): List<Aktivitet.Aktivitetskort>

    fun oppdaterOppgave(oppgave: Aktivitet.Oppgave)
    fun hentAktiviteter(hashetFnr: ByteArray, orgnr: String): List<Aktivitet>
}