package db

import domene.Aktivitet
import java.time.Instant

class AktivitetDto(
    val hashetFodselsnummer: ByteArray,
    val orgnr: String,
    val aktivitetsid: String,
    val aktivitetstype: Aktivitetstype,
    val fullført: Boolean?,
    val fullføringstidspunkt: Instant?,
    val status: String?,
) {
    companion object {
        fun fraDomene(aktivitet: Aktivitet) =
            when (aktivitet) {
                is Aktivitet.Oppgave -> AktivitetDto(
                    hashetFodselsnummer = aktivitet.hashetFodselsnummer,
                    orgnr = aktivitet.orgnr,
                    aktivitetsid = aktivitet.aktivitetsid,
                    aktivitetstype = Aktivitetstype.OPPGAVE,
                    status = aktivitet.status.toString(),
                    fullført = null,
                    fullføringstidspunkt = null,
                )

                is Aktivitet.Teoriseksjon -> AktivitetDto(
                    hashetFodselsnummer = aktivitet.hashetFodselsnummer,
                    orgnr = aktivitet.orgnr,
                    aktivitetsid = aktivitet.aktivitetsid,
                    aktivitetstype = Aktivitetstype.TEORISEKSJON,
                    status = aktivitet.status.toString(),
                    fullført = null,
                    fullføringstidspunkt = null,
                )
            }
    }

    fun tilDomene(): Aktivitet? =
        when (aktivitetstype) {
            Aktivitetstype.AKTIVITETSKORT -> null

            Aktivitetstype.OPPGAVE -> Aktivitet.Oppgave(
                hashetFodselsnummer,
                orgnr,
                aktivitetsid,
                Aktivitet.Oppgave.Status.valueOf(status!!),
            )

            Aktivitetstype.TEORISEKSJON -> Aktivitet.Teoriseksjon(
                hashetFodselsnummer,
                orgnr,
                aktivitetsid,
                Aktivitet.Teoriseksjon.Status.valueOf(status!!),
            )
        }

    enum class Aktivitetstype {
        TEORISEKSJON,
        OPPGAVE,
        AKTIVITETSKORT,
    }
}
