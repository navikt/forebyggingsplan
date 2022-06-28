import arrow.core.None
import domene.Aktivitet
import domene.Arbeidsgiver
import domene.ForeslåttAktivitet.Companion.foreslåAktivitetForArbeidsgiver
import domene.NavAnsatt
import domene.ValgtAktivitet.Companion.velgForeslåttAktivitet
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
        val navAnsatt = NavAnsatt("Z123456")
        val arbeidsgiver = Arbeidsgiver(fnr = "12345678911", orgnr = "123456789")

        val foreslåttAktivitet = navAnsatt.foreslåAktivitetForArbeidsgiver(aktivitet, arbeidsgiver)
        foreslåttAktivitet.aktivitet shouldBe aktivitet
        foreslåttAktivitet.foreslåttAv shouldBe navAnsatt
        foreslåttAktivitet.foreslåttFor shouldBe arbeidsgiver

        val valgtAktivitet = arbeidsgiver.velgForeslåttAktivitet(foreslåttAktivitet)
        valgtAktivitet.foreslåttAktivitet shouldBe foreslåttAktivitet
        valgtAktivitet.foreslåttAktivitet.aktivitet shouldBe aktivitet
        valgtAktivitet.valgtAv shouldBe arbeidsgiver

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
