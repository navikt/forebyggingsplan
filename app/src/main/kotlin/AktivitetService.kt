import db.AktivitetRepository
import domene.*
import domene.ValgtAktivitet.Companion.velgAktivitet

class AktivitetService(private val aktivitetRepository: AktivitetRepository) {

    fun hentAktiviteter() = aktivitetRepository.hentAktiviteter()

    fun velgAktivitet(
        arbeidsgiverRepresentant: ArbeidsgiverRepresentant,
        aktivitet: Aktivitet
    ) = arbeidsgiverRepresentant.velgAktivitet(aktivitet).lagre()

    private fun ValgtAktivitet.lagre() = aktivitetRepository.lagreValgtAktivitet(this)
}
