import domene.Aktivitet
import domene.AktivitetsType
import domene.ArbeidsgiverRepresentant
import domene.ValgtAktivitet.Companion.velgAktivitet
import domene.Virksomhet
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AktivitetTest {

    @Test
    fun `en aktivitet kan opprettes, velges, og en arbeidsgiver kan oppdatere frist og fullføre den`() {
        val aktivitet = Aktivitet(
            tittel = "Videokurs om mestring på jobb",
            beskrivelse = "En lang beskrivelse",
            type = AktivitetsType.Kurs,
            mål = "Et mål"
        )
        val virksomhet = Virksomhet(orgnr = "123456789")
        val arbeidsgiverRepresentant = ArbeidsgiverRepresentant(fnr = "12345678911", virksomhet = virksomhet)


        val valgtAktivitet = arbeidsgiverRepresentant.velgAktivitet(aktivitet)
        valgtAktivitet.aktivitet shouldBe aktivitet
        valgtAktivitet.valgtAv shouldBe arbeidsgiverRepresentant

        valgtAktivitet.erFullført().shouldBeFalse()
        valgtAktivitet.fullførAktivitet()
        valgtAktivitet.erFullført().shouldBeTrue()
    }
}
