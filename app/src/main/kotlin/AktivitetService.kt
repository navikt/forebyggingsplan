import db.AktivitetRepository
import domene.*

class AktivitetService(private val aktivitetRepository: AktivitetRepository) {

    fun hentValgteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        aktivitetRepository.hentValgteAktiviteterForVirksomhet(virksomhet)

    fun lagreAktivitet(aktivitet: ValgtAktivitet) = aktivitetRepository.lagreValgtAktivitet(valgtAktivitet = aktivitet)

    fun hentFullførteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        aktivitetRepository.hentFullførteAktiviteterForVirksomhet(virksomhet)

    fun fullførAktivitet(aktivitetId: Int, orgnr: String) {
        aktivitetRepository.fullfør(aktivitetId = aktivitetId, orgnr = orgnr)
    }
}
