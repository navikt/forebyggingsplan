package application

import db.AktiviteterRepository
import domene.Aktivitet
import domene.Virksomhet
import kotlinx.datetime.Clock
import util.hash.Hasher

class AktivitetService(
    private val aktivitetRepository: AktiviteterRepository,
    private val hasher: Hasher,
) {
    fun fullførAktivitet(fødselsnummer: String, aktivitetsid: String, orgnr: String) {
        val hashetFødselsnummer = hasher.hash(fødselsnummer)
        val fullføringstidspunkt = Clock.System.now()
        val aktivitet =
            Aktivitet.Aktivitetskort(hashetFødselsnummer, orgnr, aktivitetsid, true, fullføringstidspunkt)
        aktivitetRepository.settAktivitet(aktivitet)
    }

    fun hentAlleFullførteAktiviteterFor(fnr: String, virksomhet: Virksomhet): List<Aktivitet.Aktivitetskort> {
        val hashetFnr = hasher.hash(fnr)
        return aktivitetRepository.hentAlleFullførteAktiviteterFor(hashetFnr, virksomhet.orgnr)
    }

    fun oppdaterOppgave(oppgave: Aktivitet.Oppgave) {
        return aktivitetRepository.oppdaterOppgave(oppgave)
    }

    fun hentAktiviteter(fnr: String, orgnr: String): List<Aktivitet> {
        val hashetFnr = hasher.hash(fnr)
        return aktivitetRepository.hentAktiviteter(hashetFnr, orgnr)
    }
}