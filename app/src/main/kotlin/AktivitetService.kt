import db.AktiviteterRepository
import db.ValgtAktivitetRepository
import domene.Aktivitet
import domene.ValgtAktivitet
import domene.Virksomhet
import kotlinx.datetime.LocalDate
import util.hash.Sha3Hasher

class AktivitetService(private val aktivitetRepository: ValgtAktivitetRepository) {

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

    fun hentAlleFullførteAktiviteterFor(fnr: String, virksomhet: Virksomhet): List<Aktivitet> {
        val hashetFnr = Sha3Hasher().hash(fnr)
        return AktiviteterRepository.hentAlleFullførteAktiviteterFor(hashetFnr, virksomhet.orgnr)
    }
}
