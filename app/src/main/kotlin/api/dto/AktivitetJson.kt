package api.dto

import domene.Aktivitet
import kotlinx.serialization.Serializable

@Serializable
data class AktivitetJson(
    val aktivitetId: String,
    val aktivitetType: Aktivitetstype,
    val status: Oppgavestatus?,
) {
    enum class Aktivitetstype {
        AKTIVITETSKORT, OPPGAVE;
    }

    enum class Oppgavestatus {
        STARTET, FULLFØRT, AVBRUTT;

        companion object {
            fun fraDomene(status: Aktivitet.Oppgave.Status) = when (status) {
                Aktivitet.Oppgave.Status.STARTET -> STARTET
                Aktivitet.Oppgave.Status.FULLFØRT -> FULLFØRT
                Aktivitet.Oppgave.Status.AVBRUTT -> AVBRUTT
            }
        }
    }

    companion object {
        fun fraDomene(aktivitet: Aktivitet) = when (aktivitet) {
            is Aktivitet.Aktivitetskort -> AktivitetJson(
                aktivitetId = aktivitet.aktivitetsid,
                aktivitetType = Aktivitetstype.AKTIVITETSKORT,
                status = null
            )
            is Aktivitet.Oppgave -> AktivitetJson(
                aktivitetId = aktivitet.aktivitetsid,
                aktivitetType = Aktivitetstype.OPPGAVE,
                status = Oppgavestatus.fraDomene(aktivitet.status)
            )
        }
    }
}
