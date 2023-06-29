package application

import db.AktiviteterRepository
import domene.Aktivitet
import kotlinx.datetime.Clock
import util.hash.Hasher

class AktivitetService(
    private val aktivitetRepository: AktiviteterRepository,
    private val hasher: Hasher,
) {
    fun fullførAktivitet(fødselsnummer: String, aktivitetsid: String, orgnr: String, aktivitetsversjon: String) {
        val hashetFødselsnummer = hasher.hash(fødselsnummer)
        val fullføringstidspunkt = Clock.System.now()
        val aktivitet =
            Aktivitet(hashetFødselsnummer, orgnr, aktivitetsid, aktivitetsversjon, true, fullføringstidspunkt)
        aktivitetRepository.settAktivitet(aktivitet)
    }

}