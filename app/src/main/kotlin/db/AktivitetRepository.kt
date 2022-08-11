package db

import domene.*

class AktivitetRepository {

    fun hentAktiviteter() = aktiviteter

    fun hentAktivitet(aktivitetsId: String) = aktiviteter.find { it.id == aktivitetsId }

    fun hentValgteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        valgteAktiviteter.filter { it.valgtAv.virksomhet == virksomhet}

    fun lagreValgtAktivitet(valgAktivitet: ValgtAktivitet): ValgtAktivitet {
        valgteAktiviteter.add(valgAktivitet)
        return valgAktivitet
    }

    fun hentFullførteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        fullførteAktiviteter.filter { it.fullførtAv.virksomhet == virksomhet}

    // MOCK DATA INNTIL VI HAR DB PÅ PLASS
    private var aktiviteter: List<Aktivitet> = listOf(
        Aktivitet(
            tittel = "Pilotering av medarbeidersamtalen"
        ),
        Aktivitet(
            tittel = "Kartleggingsmøte med ansatt"
        ),
        Aktivitet(
            tittel = "Sinnemestring"
        ),
        Aktivitet(
            tittel = "Hvordan ta den vanskelige praten?"
        ),
    )
    private val virksomhet = Virksomhet("123456789")

    private val arbeidsgiverRepresentant = ArbeidsgiverRepresentant(fnr = "12345678912", virksomhet = virksomhet)
    private var valgteAktiviteter : MutableList<ValgtAktivitet> = mutableListOf()
    private var fullførteAktiviteter : MutableList<FullførtAktivitet> = mutableListOf()
    // SLUTT MOCK DATA

}
