package api.dto

import domene.AktivitetsType
import kotlinx.serialization.Serializable

@Serializable
data class AktivitetDTO(
    val id: String,
    val tittel: String,
    val beskrivelse: String,
    val type: AktivitetsType,
    val mål: String
)
