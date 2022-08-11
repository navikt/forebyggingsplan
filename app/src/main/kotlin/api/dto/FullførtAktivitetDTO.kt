package api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
data class FullførtAktivitetDTO(
    val valgtAktivitet: ValgtAktivitetDTO,
    val fullførtTidspunkt: Instant,
    val fullførtAv: ArbeidsgiverRepresentantDTO
)

