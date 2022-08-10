import db.AktivitetRepository
import domene.*
import domene.ValgtAktivitet.Companion.velgAktivitet

class AktivitetService(private val aktivitetRepository: AktivitetRepository) {

    fun hentAktiviteter() = aktivitetRepository.hentAktiviteter()

    fun velgAktivitet(aktivitetsId: String, arbeidsgiverRepresentant: ArbeidsgiverRepresentant): ValgtAktivitet {

        val aktivitet = aktivitetRepository.hentAktivitet(aktivitetsId) ?: throw AktivitetIkkeFunnetException(aktivitetsId = aktivitetsId)
        return velgAktivitet(aktivitet = aktivitet, arbeidsgiverRepresentant = arbeidsgiverRepresentant)
    }

    fun hentValgteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        aktivitetRepository.hentValgteAktiviteterForVirksomhet(virksomhet)

    private fun velgAktivitet(
        arbeidsgiverRepresentant: ArbeidsgiverRepresentant,
        aktivitet: Aktivitet
    ) = arbeidsgiverRepresentant.velgAktivitet(aktivitet).lagre()

    private fun ValgtAktivitet.lagre() = aktivitetRepository.lagreValgtAktivitet(this)
}
