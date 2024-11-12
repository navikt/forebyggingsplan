package api.endepunkt.json

import domene.Aktivitet
import kotlinx.serialization.Serializable

@Serializable
data class AktivitetJson(
    val aktivitetId: String,
    val aktivitetType: Aktivitetstype,
    val status: Status?,
) {
    enum class Status {
        STARTET,
        FULLFØRT,
        AVBRUTT,
        LEST,
        ULEST,
        ;

        companion object {
            fun fraDomene(status: Aktivitet.Oppgave.Status) =
                when (status) {
                    Aktivitet.Oppgave.Status.STARTET -> STARTET
                    Aktivitet.Oppgave.Status.FULLFØRT -> FULLFØRT
                    Aktivitet.Oppgave.Status.AVBRUTT -> AVBRUTT
                }

            fun fraDomene(status: Aktivitet.Teoriseksjon.Status) =
                when (status) {
                    Aktivitet.Teoriseksjon.Status.LEST -> LEST
                    Aktivitet.Teoriseksjon.Status.ULEST -> ULEST
                }
        }
    }

    companion object {
        fun fraDomene(aktivitet: Aktivitet) =
            when (aktivitet) {
                is Aktivitet.Oppgave -> AktivitetJson(
                    aktivitetId = aktivitet.aktivitetsid,
                    aktivitetType = Aktivitetstype.OPPGAVE,
                    status = Status.fraDomene(aktivitet.status),
                )

                is Aktivitet.Teoriseksjon -> AktivitetJson(
                    aktivitetId = aktivitet.aktivitetsid,
                    aktivitetType = Aktivitetstype.TEORISEKSJON,
                    status = Status.fraDomene(aktivitet.status),
                )
            }
    }
}
