import arrow.core.None
import domene.Aktivitet
import domene.ArbeidsgiverRepresentant
import domene.ForeslåttAktivitet.Companion.foreslåAktivitetForVirksomhet
import domene.ValgtAktivitet.Companion.velgForeslåttAktivitet
import domene.Virksomhet
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.time.Duration
import kotlin.test.Test

class AktivitetTest {

    @Test
    fun `en aktivitet kan opprettes, foreslås, velges, og en arbeidsgiver kan oppdatere frist og fullføre den`() {
        val aktivitet = Aktivitet(
            tittel = "Videokurs om mestring på jobb",
            beskrivelse = "En lang beskrivelse",
            type = Aktivitet.AktivitetsType.Kurs,
            mål = "Et mål"
        )
        val virksomhet = Virksomhet(orgnr = "123456789")
        val arbeidsgiverRepresentant = ArbeidsgiverRepresentant(fnr = "12345678911", virksomhet = virksomhet)

        val foreslåttAktivitet = foreslåAktivitetForVirksomhet(aktivitet, virksomhet)
        foreslåttAktivitet.aktivitet shouldBe aktivitet
        foreslåttAktivitet.foreslåttFor shouldBe virksomhet

        val valgtAktivitet = arbeidsgiverRepresentant.velgForeslåttAktivitet(foreslåttAktivitet)
        valgtAktivitet.foreslåttAktivitet shouldBe foreslåttAktivitet
        valgtAktivitet.foreslåttAktivitet.aktivitet shouldBe aktivitet
        valgtAktivitet.valgtAv shouldBe arbeidsgiverRepresentant

        valgtAktivitet.fristForÅFullføreAktivitet shouldBe None
        val nyFrist = Duration.ofHours(72)
        valgtAktivitet.endreFristForÅFullføreAktivitet(duration = nyFrist)
        valgtAktivitet.fristForÅFullføreAktivitet.isDefined().shouldBeTrue()
        valgtAktivitet.fristForÅFullføreAktivitet.tap { it shouldBe nyFrist }

        valgtAktivitet.erFullført().shouldBeFalse()
        valgtAktivitet.fullførAktivitet()
        valgtAktivitet.erFullført().shouldBeTrue()
    }
}
