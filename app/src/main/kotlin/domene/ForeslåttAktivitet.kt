package domene

import java.time.Instant

class ForeslåttAktivitet private constructor(
    val aktivitet: Aktivitet,
    val foreslåttFor: Virksomhet,
) {
    private val foreslåttTidspunkt = Instant.now()

    companion object {
        fun foreslåAktivitetForVirksomhet(aktivitet: Aktivitet, virksomhet: Virksomhet) =
            ForeslåttAktivitet(aktivitet = aktivitet, foreslåttFor = virksomhet)
    }
}
