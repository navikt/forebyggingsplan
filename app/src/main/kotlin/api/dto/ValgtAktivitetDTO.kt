package api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ValgtAktivitetDTO(
    val aktivitet: AktivitetDTO,
    val valgtTidspunkt: Instant,
    val valgtAv: ArbeidsgiverRepresentantDTO
)
