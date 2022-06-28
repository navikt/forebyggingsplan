package domene

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import java.time.Duration
import java.time.Instant

class ValgtAktivitet private constructor(
    val foreslåttAktivitet: ForeslåttAktivitet,
    val valgtAv: Arbeidsgiver,
) {
    private val valgtTidspunkt = Instant.now()

    var fristForÅFullføreAktivitet: Option<Duration> = None
        private set

    private var fullført: Boolean = false
    private var fullførtTidspunkt: Option<Instant> = None

    fun erFullført() = this.fullført

    fun fullførAktivitet() {
        this.fullført = true
        this.fullførtTidspunkt = Some(Instant.now())
    }

    fun endreFristForÅFullføreAktivitet(duration: Duration) {
        this.fristForÅFullføreAktivitet = Some(duration)
    }

    companion object {
        fun Arbeidsgiver.velgForeslåttAktivitet(foreslåttAktivitet: ForeslåttAktivitet) =
            ValgtAktivitet(foreslåttAktivitet = foreslåttAktivitet, valgtAv = this)
    }
}
