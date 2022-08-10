package domene

import api.dto.ValgtAktivitetDTO
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlinx.datetime.toKotlinInstant
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

    fun tilDto(): ValgtAktivitetDTO = ValgtAktivitetDTO(
        aktivitet = aktivitet.tilDto(),
        valgtTidspunkt = valgtTidspunkt.toKotlinInstant(),
        valgtAv = valgtAv.tilDto()
    )

    companion object {
        fun ArbeidsgiverRepresentant.velgAktivitet(aktivitet: Aktivitet) =
            ValgtAktivitet(aktivitet = aktivitet, valgtAv = this)
    }
}
