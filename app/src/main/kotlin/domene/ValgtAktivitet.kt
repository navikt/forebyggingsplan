package domene

import api.dto.ValgtAktivitetDTO
import domene.FullførtAktivitet.Companion.fraValgtAktivitet
import kotlinx.datetime.toKotlinInstant
import java.time.Instant

class ValgtAktivitet private constructor(
    val aktivitet: Aktivitet,
    val valgtAv: ArbeidsgiverRepresentant,
) {
    private val valgtTidspunkt = Instant.now()

    fun fullførAktivitet() = fraValgtAktivitet(this)

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
