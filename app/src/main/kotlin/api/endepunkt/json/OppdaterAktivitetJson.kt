package api.endepunkt.json

import domene.Aktivitet
import kotlinx.serialization.Serializable

@Serializable
data class OppdaterAktivitetJson(
    val aktivitetstype: Aktivitetstype?,
    val status: String,
) {
    fun tilDomene(
        hashetFodselsnummer: ByteArray,
        orgnr: String,
        aktivitetsid: String,
    ): Aktivitet {
        val statusUppercase = status.uppercase()
        return when (aktivitetstype) {
            Aktivitetstype.TEORISEKSJON -> Aktivitet.Teoriseksjon(
                hashetFodselsnummer,
                orgnr,
                aktivitetsid,
                Aktivitet.Teoriseksjon.Status.valueOf(statusUppercase),
            )
            Aktivitetstype.OPPGAVE -> Aktivitet.Oppgave(
                hashetFodselsnummer,
                orgnr,
                aktivitetsid,
                Aktivitet.Oppgave.Status.valueOf(statusUppercase),
            )
            null -> Aktivitet.Oppgave(hashetFodselsnummer, orgnr, aktivitetsid, Aktivitet.Oppgave.Status.valueOf(statusUppercase))
        }
    }
}
