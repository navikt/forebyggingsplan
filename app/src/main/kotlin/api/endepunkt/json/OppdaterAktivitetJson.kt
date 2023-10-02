package api.endepunkt.json

import domene.Aktivitet
import kotlinx.serialization.Serializable

@Serializable
data class OppdaterAktivitetJson(
    val aktivitetstype: Aktivitetstype?,
    val status: String
) {
    fun tilDomene(hashetFodselsnummer: ByteArray, orgnr: String, aktivitetsid: String): Aktivitet = when (aktivitetstype) {
        Aktivitetstype.TEORISEKSJON -> Aktivitet.Teoriseksjon(
            hashetFodselsnummer, orgnr, aktivitetsid, Aktivitet.Teoriseksjon.Status.valueOf(status)
        )
        Aktivitetstype.OPPGAVE -> Aktivitet.Oppgave(
            hashetFodselsnummer, orgnr, aktivitetsid, Aktivitet.Oppgave.Status.valueOf(status)
        )
        null -> Aktivitet.Oppgave(hashetFodselsnummer, orgnr, aktivitetsid, Aktivitet.Oppgave.Status.valueOf(status))
    }
}
