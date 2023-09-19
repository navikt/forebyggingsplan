import db.ValgtAktivitetRepository
import domene.ValgtAktivitet
import domene.Virksomhet
import kotlinx.datetime.LocalDate

@Deprecated("Skal erstattes av personlige aktiviteter")
class LegacyAktivitetService(private val aktivitetRepository: ValgtAktivitetRepository) {

    fun hentValgteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        aktivitetRepository.hentValgteAktiviteterForVirksomhet(virksomhet)

    fun lagreAktivitet(aktivitet: ValgtAktivitet) =
        aktivitetRepository.lagreValgtAktivitet(valgtAktivitet = aktivitet)

    fun fullførAktivitet(valgtAktivitet: ValgtAktivitet) =
        aktivitetRepository.fullfør(valgtAktivitet = valgtAktivitet)

    fun endreFrist(virksomhet: Virksomhet, aktivitetsId: Int, frist: LocalDate?) {
        val valgtAktivitet = hentValgtAktivitet(virksomhet, aktivitetsId)
        aktivitetRepository.endreFrist(valgtAktivitet, frist)
    }

    fun hentValgtAktivitet(virksomhet: Virksomhet, aktivitetsId: Int) =
        aktivitetRepository.hentValgtAktivitet(virksomhet, aktivitetsId)
}