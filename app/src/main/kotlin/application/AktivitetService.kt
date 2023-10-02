package application

import db.AktiviteterRepository
import domene.Aktivitet
import util.hash.Hasher

class AktivitetService(
    private val aktivitetRepository: AktiviteterRepository,
    private val hasher: Hasher,
) {
    fun oppdaterAktivitet(aktivitet: Aktivitet) {
        return when (aktivitet) {
            is Aktivitet.Oppgave -> aktivitetRepository.oppdaterOppgave(aktivitet)
            is Aktivitet.Teoriseksjon -> TODO()
        }
    }

    fun hentAktiviteter(fnr: String, orgnr: String): List<Aktivitet> {
        val hashetFnr = hasher.hash(fnr)
        return aktivitetRepository.hentAktiviteter(hashetFnr, orgnr)
    }
}