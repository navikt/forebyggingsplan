package api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArbeidsgiverRepresentantDTO(
    val fnr: String,
    val orgnr: String,
)
