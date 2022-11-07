import db.AktivitetRepository
import domene.*

class AktivitetService(private val aktivitetRepository: AktivitetRepository) {

    fun hentValgteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        aktivitetRepository.hentValgteAktiviteterForVirksomhet(virksomhet)

    fun lagreAktivitet(aktivitet: ValgtAktivitet) = aktivitetRepository.lagreValgtAktivitet(valgtAktivitet = aktivitet)

    fun fullførAktivitet(valgtAktivitet: ValgtAktivitet) =
        aktivitetRepository.fullfør(valgtAktivitet = valgtAktivitet)

    fun hentValgtAktivitet(virksomhet: Virksomhet, aktivitetsId: Int) =
        aktivitetRepository.hentValgtAktivitet(virksomhet, aktivitetsId)
}
