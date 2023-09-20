package api.dto

import domene.Aktivitet
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FullførtAktivitetJson(
    val aktivitetsId: String,
    val fullført: Boolean,
    val fullførtTidspunkt: Instant?,
) {
    companion object {
        fun fraDomene(aktivitetskort: Aktivitet.Aktivitetskort) = FullførtAktivitetJson(
            aktivitetsId = aktivitetskort.aktivitetsid,
            fullført = aktivitetskort.fullført,
            fullførtTidspunkt = aktivitetskort.fullføringstidspunkt
        )
    }
}
