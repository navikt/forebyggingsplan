package db

import domene.Aktivitet
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.time.Instant

data class AktivitetDto(
    val hashetFodselsnummer: ByteArray,
    val orgnr: String,
    val aktivitetsid: String,
    val aktivitetstype: Aktivitetstype,
    val fullført: Boolean?,
    val fullføringstidspunkt: Instant?,
    val status: String?,
) {

    companion object {
        fun fromDomain(aktivitet: Aktivitet) = when (aktivitet) {
            is Aktivitet.Aktivitetskort -> AktivitetDto(
                hashetFodselsnummer = aktivitet.hashetFodselsnummer,
                orgnr = aktivitet.orgnr,
                aktivitetsid = aktivitet.aktivitetsid,
                aktivitetstype = Aktivitetstype.AKTIVITETSKORT,
                fullført = aktivitet.fullført,
                fullføringstidspunkt = aktivitet.fullføringstidspunkt?.toJavaInstant(),
                status = null,
            )

            is Aktivitet.Oppgave -> AktivitetDto(
                hashetFodselsnummer = aktivitet.hashetFodselsnummer,
                orgnr = aktivitet.orgnr,
                aktivitetsid = aktivitet.aktivitetsid,
                aktivitetstype = Aktivitetstype.OPPGAVE,
                status = aktivitet.status.toString(),
                fullført = null,
                fullføringstidspunkt = null,
            )
        }
    }

    fun tilDomene(): Aktivitet = when (aktivitetstype) {
        Aktivitetstype.AKTIVITETSKORT -> Aktivitet.Aktivitetskort(
            hashetFodselsnummer, orgnr, aktivitetsid, fullført!!, fullføringstidspunkt?.toKotlinInstant()
        )

        Aktivitetstype.OPPGAVE -> Aktivitet.Oppgave(
            hashetFodselsnummer, orgnr, aktivitetsid, Aktivitet.Oppgave.Status.valueOf(status!!)
        )
    }


    enum class Aktivitetstype {
        AKTIVITETSKORT, OPPGAVE
    }
}