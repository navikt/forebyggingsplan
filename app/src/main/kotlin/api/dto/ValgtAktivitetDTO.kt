package api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class OpprettValgtAktivitetDTO(
    val aktivitetsmalId: String,
    val orgnr: String
)

@Serializable
data class ValgtAktivitetDTO(
    val aktivitetsmalId: String,
    val valgtTidspunkt: Instant,
    val valgtAv: ArbeidsgiverRepresentantDTO
)
