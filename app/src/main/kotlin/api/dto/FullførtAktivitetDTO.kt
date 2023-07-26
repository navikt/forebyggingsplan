package api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FullførtAktivitetDTO(
    val orgnr: String,
    val aktivitetsId: String,
    val aktivitetsversjon: String,
    val fullført: Boolean,
    val fullførtTidspunkt: Instant?,
)
