package api.dto

import kotlinx.serialization.Serializable

@Serializable
data class FullførValgtAktivitetDTO (
    val aktivitetsId: Int?,
    val aktivitetsmalId: String,
)
