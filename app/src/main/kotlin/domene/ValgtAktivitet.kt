package domene

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import java.time.Instant

class ValgtAktivitet private constructor(
    val aktivitet: Aktivitet,
    val valgtAv: ArbeidsgiverRepresentant,
) {
    private val valgtTidspunkt = Instant.now()

    private var fullført: Boolean = false
    private var fullførtTidspunkt: Option<Instant> = None

    fun erFullført() = this.fullført

    fun fullførAktivitet() {
        this.fullført = true
        this.fullførtTidspunkt = Some(Instant.now())
    }

    companion object {
        fun ArbeidsgiverRepresentant.velgAktivitet(aktivitet: Aktivitet) =
            ValgtAktivitet(aktivitet = aktivitet, valgtAv = this)
    }
}
