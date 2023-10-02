package db

import domene.Aktivitet

interface AktiviteterRepository {
    fun oppdaterAktivitet(aktivitet: Aktivitet)
    fun hentAktiviteter(hashetFnr: ByteArray, orgnr: String): List<Aktivitet>
}