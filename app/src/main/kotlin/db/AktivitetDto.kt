package db

import domene.Aktivitet
import kotlinx.datetime.toJavaInstant
import java.time.Instant

class AktivitetDto(
    aktivitet: Aktivitet
) {
    val hashetFodselsnummer: ByteArray = aktivitet.hashetFodselsnummer
    val orgnr: String = aktivitet.orgnr
    val aktivitetsid: String = aktivitet.aktivitetsid
    val aktivitetstype: Aktivitetstype

    val fullført: Boolean?
    val fullføringstidspunkt: Instant?
    val status: String?

    enum class Aktivitetstype {
        AKTIVITETSKORT, OPPGAVE
    }

    init {
        when (aktivitet) {
            is Aktivitet.Aktivitetskort -> {
                fullført = aktivitet.fullført
                fullføringstidspunkt = aktivitet.fullføringstidspunkt?.toJavaInstant()
                aktivitetstype = Aktivitetstype.AKTIVITETSKORT

                status = null
            }

            is Aktivitet.Oppgave -> {
                status = aktivitet.status.toString()
                aktivitetstype = Aktivitetstype.OPPGAVE

                fullført = null
                fullføringstidspunkt = null
            }
        }
    }
}