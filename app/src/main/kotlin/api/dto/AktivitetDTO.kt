package api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AktivitetDTO(
    val id: String,
    val tittel: String
)
