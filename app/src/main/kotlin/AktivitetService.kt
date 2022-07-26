import db.AktivitetRepository
import domene.ArbeidsgiverRepresentant
import domene.ForeslåttAktivitet
import domene.ValgtAktivitet
import domene.ValgtAktivitet.Companion.velgForeslåttAktivitet
import domene.Virksomhet

class AktivitetService(private val aktivitetRepository: AktivitetRepository) {

    fun hentAktiviteter() = aktivitetRepository.hentAktiviteter()
    fun hentAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        aktivitetRepository.hentAktiviteterForVirksomhet(virksomhet = virksomhet)

    fun velgForeslåttAktivitet(
        arbeidsgiverRepresentant: ArbeidsgiverRepresentant,
        foreslåttAktivitet: ForeslåttAktivitet
    ) = arbeidsgiverRepresentant.velgForeslåttAktivitet(foreslåttAktivitet).lagre()

    private fun ValgtAktivitet.lagre() = aktivitetRepository.lagreValgtAktivitet(this)
}
