package api.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FullførtAktivitetDTO(
    val aktivitetsId: String,
    val aktivitetsversjon: String,
    val fullført: Boolean,
    val fullførtTidspunkt: Instant?,
)
