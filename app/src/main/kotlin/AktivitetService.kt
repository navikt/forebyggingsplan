import db.AktivitetRepository
import domene.*
import domene.ValgtAktivitet.Companion.velgAktivitet
import exceptions.AktivitetIkkeFunnetException

class AktivitetService(private val aktivitetRepository: AktivitetRepository) {

    fun hentAktivitetsmaler() = aktivitetRepository.hentAktivitetsmaler()

    fun velgAktivitet(aktivitetsmalId: String, arbeidsgiverRepresentant: ArbeidsgiverRepresentant): ValgtAktivitet {
        val aktivitetsmal = aktivitetRepository.hentAktivitetsmal(aktivitetsmalId) ?: throw AktivitetIkkeFunnetException(aktivitetsmalId = aktivitetsmalId)
        return velgAktivitet(aktivitetsmal = aktivitetsmal, arbeidsgiverRepresentant = arbeidsgiverRepresentant)
    }

    fun hentValgteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        aktivitetRepository.hentValgteAktiviteterForVirksomhet(virksomhet)

    fun hentFullførteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        aktivitetRepository.hentFullførteAktiviteterForVirksomhet(virksomhet)

    private fun velgAktivitet(
        arbeidsgiverRepresentant: ArbeidsgiverRepresentant,
        aktivitetsmal: Aktivitetsmal
    ) = arbeidsgiverRepresentant.velgAktivitet(aktivitetsmal).lagre()

    private fun ValgtAktivitet.lagre() = aktivitetRepository.lagreValgtAktivitet(this)
}
