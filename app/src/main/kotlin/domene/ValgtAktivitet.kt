package domene

import api.dto.ValgtAktivitetDTO
import kotlinx.datetime.toKotlinInstant
import java.time.Instant

class ValgtAktivitet private constructor(
    val id: Int = 0,
    val aktivitetsmal: Aktivitetsmal,
    val valgtAv: ArbeidsgiverRepresentant,
    val fullført: Boolean = false,
    val fullførtTidspunkt: Instant? = null,
    val opprettelsesTidspunkt: Instant = Instant.now()
) {

    fun tilDto(): ValgtAktivitetDTO = ValgtAktivitetDTO(
        id = id,
        aktivitetsmalId = aktivitetsmal.id,
        valgtAv = valgtAv.tilDto(),
        fullført = fullført,
        fullførtTidspunkt = fullførtTidspunkt?.toKotlinInstant(),
        opprettelsesTidspunkt = opprettelsesTidspunkt.toKotlinInstant()
    )

    companion object {
        fun ArbeidsgiverRepresentant.velgAktivitet(
            aktivitetsmal: Aktivitetsmal,
            id: Int = 0,
            fullført: Boolean = false,
            fullførtTidspunkt: Instant? = null,
            opprettelsesTidspunkt: Instant = Instant.now(),
        ) =
            ValgtAktivitet(
                aktivitetsmal = aktivitetsmal,
                valgtAv = this,
                id = id,
                fullført = fullført,
                fullførtTidspunkt = fullførtTidspunkt,
                opprettelsesTidspunkt = opprettelsesTidspunkt,
            )
    }
}
