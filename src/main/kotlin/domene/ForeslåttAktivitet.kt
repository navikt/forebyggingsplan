package domene

import java.time.Instant

class ForeslåttAktivitet private constructor(
    val aktivitet: Aktivitet,
    val foreslåttAv: NavAnsatt,
    val foreslåttFor: Arbeidsgiver,
) {
    private val foreslåttTidspunkt: Instant = Instant.now()

    companion object {
        fun NavAnsatt.foreslåAktivitetForArbeidsgiver(aktivitet: Aktivitet, arbeidsgiver: Arbeidsgiver) =
            ForeslåttAktivitet(aktivitet = aktivitet, foreslåttAv = this, foreslåttFor = arbeidsgiver)
    }
}
