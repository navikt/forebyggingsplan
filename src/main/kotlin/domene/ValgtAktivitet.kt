package domene

import arrow.core.None
import arrow.core.Option
import java.time.Duration
import java.time.Instant

class ValgtAktivitet(
    private val orgnr: String,
    private val aktivitet: Aktivititet,
    private val opprettetAv: Person,
    private val fullført: Boolean = false,
) {
    private val id = "some id"
    private val opprettetTidspunkt: Instant = Instant.now()
    private var fristForÅFullføreAktivitet: Option<Duration> = None
}
