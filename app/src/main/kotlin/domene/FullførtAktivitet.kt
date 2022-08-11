package domene

import api.dto.FullførtAktivitetDTO
import kotlinx.datetime.toKotlinInstant
import java.time.Instant

class FullførtAktivitet private constructor(
    val valgtAktivitet: ValgtAktivitet,
    val fullførtAv: ArbeidsgiverRepresentant
){
    val fullførtTidspunkt = Instant.now()

    companion object {
        fun fraValgtAktivitet(valgtAktivitet: ValgtAktivitet) =
            FullførtAktivitet(valgtAktivitet = valgtAktivitet, fullførtAv = valgtAktivitet.valgtAv)
    }

    fun tilDto() =
        FullførtAktivitetDTO(
            valgtAktivitet = valgtAktivitet.tilDto(),
            fullførtTidspunkt = fullførtTidspunkt.toKotlinInstant(),
            fullførtAv = fullførtAv.tilDto()
        )
}
