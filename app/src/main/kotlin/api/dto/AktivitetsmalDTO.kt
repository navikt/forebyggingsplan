package api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AktivitetsmalDTO(
    val id: String,
    val tittel: String
)
