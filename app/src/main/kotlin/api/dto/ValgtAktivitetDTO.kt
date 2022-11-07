package api.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class OpprettValgtAktivitetDTO(
    val aktivitetsmalId: String,
    val orgnr: String
)

@Serializable
data class ValgtAktivitetDTO(
    val id: Int,
    val aktivitetsmalId: String,
    val valgtAv: ArbeidsgiverRepresentantDTO,
    val fullført: Boolean,
    val fullførtTidspunkt: Instant? = null,
    val opprettelsesTidspunkt: Instant = Clock.System.now()
)
