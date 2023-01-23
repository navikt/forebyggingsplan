import db.AktivitetRepository
import domene.*
import kotlinx.datetime.LocalDate

class AktivitetService(private val aktivitetRepository: AktivitetRepository) {

    fun hentValgteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        aktivitetRepository.hentValgteAktiviteterForVirksomhet(virksomhet)

    fun lagreAktivitet(aktivitet: ValgtAktivitet) = aktivitetRepository.lagreValgtAktivitet(valgtAktivitet = aktivitet)

    fun fullførAktivitet(valgtAktivitet: ValgtAktivitet) =
        aktivitetRepository.fullfør(valgtAktivitet = valgtAktivitet)

    fun endreFrist(virksomhet: Virksomhet, aktivitetsId: Int, frist: LocalDate?) {
        val valgtAktivitet = hentValgtAktivitet(virksomhet, aktivitetsId)
        aktivitetRepository.endreFrist(valgtAktivitet, frist)
    }

    fun hentValgtAktivitet(virksomhet: Virksomhet, aktivitetsId: Int) =
        aktivitetRepository.hentValgtAktivitet(virksomhet, aktivitetsId)
}
