package domene

import api.dto.ValgtAktivitetDTO
import kotlinx.datetime.toKotlinInstant
import java.time.Instant

class ValgtAktivitet private constructor(
    val aktivitetsmal: Aktivitetsmal,
    val valgtAv: ArbeidsgiverRepresentant,
    val fullført: Boolean = false
) {
    val valgtTidspunkt = Instant.now()

    fun tilDto(): ValgtAktivitetDTO = ValgtAktivitetDTO(
        aktivitetsmalId = aktivitetsmal.id.toString(),
        valgtTidspunkt = valgtTidspunkt.toKotlinInstant(),
        valgtAv = valgtAv.tilDto(),
        fullført = fullført
    )

     companion object {
        fun ArbeidsgiverRepresentant.velgAktivitet(aktivitetsmal: Aktivitetsmal) =
            ValgtAktivitet(aktivitetsmal = aktivitetsmal, valgtAv = this)
    }
}
