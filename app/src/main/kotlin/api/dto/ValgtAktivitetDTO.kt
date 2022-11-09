package api.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class OpprettValgtAktivitetDTO(
    val aktivitetsmalId: String,
    val frist: LocalDate? = null,
)

@Serializable
data class ValgtAktivitetDTO(
    val id: Int,
    val aktivitetsmalId: String,
    val valgtAv: ArbeidsgiverRepresentantDTO,
    val frist: LocalDate? = null,
    val fullført: Boolean,
    val fullførtTidspunkt: Instant? = null,
    val opprettelsesTidspunkt: Instant = Clock.System.now()
)
