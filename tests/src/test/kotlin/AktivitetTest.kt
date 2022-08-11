import domene.Aktivitet
import domene.AktivitetsType
import domene.ArbeidsgiverRepresentant
import domene.ValgtAktivitet.Companion.velgAktivitet
import domene.Virksomhet
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.shouldBe
import java.time.Instant
import kotlin.test.Test

class AktivitetTest {

    @Test
    fun `en aktivitet kan opprettes og velges`() {
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
        valgtAktivitet.valgtTidspunkt shouldBeBefore Instant.now()
    }

    @Test
    fun `en valgt aktivitet kan fullføres`() {
        val aktivitet = Aktivitet(
            tittel = "Videokurs om mestring på jobb",
            beskrivelse = "En lang beskrivelse",
            type = AktivitetsType.Kurs,
            mål = "Et mål"
        )
        val arbeidsgiverRepresentant = ArbeidsgiverRepresentant(fnr = "12345678911", virksomhet = Virksomhet(orgnr = "123456789"))
        val fullførtAktivitet = arbeidsgiverRepresentant.velgAktivitet(aktivitet).fullførAktivitet()
        fullførtAktivitet.fullførtAv shouldBe fullførtAktivitet.valgtAktivitet.valgtAv
        fullførtAktivitet.fullførtTidspunkt shouldBeAfter fullførtAktivitet.valgtAktivitet.valgtTidspunkt
    }
}
