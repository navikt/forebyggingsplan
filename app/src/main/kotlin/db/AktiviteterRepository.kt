package db

import domene.Aktivitet

interface AktiviteterRepository {
    fun settAktivitet(aktivitet: Aktivitet)
    fun hentAlleFullførteAktiviteterFor(
        hashetFnr: ByteArray,
        orgnr: String
    ): List<Aktivitet>
}