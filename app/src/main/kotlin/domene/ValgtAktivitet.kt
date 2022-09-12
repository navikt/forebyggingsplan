package domene

import api.dto.ValgtAktivitetDTO
import domene.FullførtAktivitet.Companion.fraValgtAktivitet
import kotlinx.datetime.toKotlinInstant
import java.time.Instant

class ValgtAktivitet private constructor(
    val aktivitetsmal: Aktivitetsmal,
    val valgtAv: ArbeidsgiverRepresentant,
) {
    val valgtTidspunkt = Instant.now()

    fun fullførAktivitet() = fraValgtAktivitet(this)

    fun tilDto(): ValgtAktivitetDTO = ValgtAktivitetDTO(
        aktivitetsmalId = aktivitetsmal.id.toString(),
        valgtTidspunkt = valgtTidspunkt.toKotlinInstant(),
        valgtAv = valgtAv.tilDto()
    )

    companion object {
        fun ArbeidsgiverRepresentant.velgAktivitet(aktivitetsmal: Aktivitetsmal) =
            ValgtAktivitet(aktivitetsmal = aktivitetsmal, valgtAv = this)
    }
}
