package api.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class EndreFristDTO(
    val aktivitetsId: Int,
    val aktivitetsmalId: String,
    val frist: LocalDate? = null
)
