package db

import domene.*

class AktivitetRepository {

    fun hentAktivitetsmaler() = aktivitetsmaler

    fun hentAktivitetsmal(aktivitetsmalId: String) = aktivitetsmaler.find { it.id == aktivitetsmalId }

    fun hentValgteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        valgteAktiviteter.filter { it.valgtAv.virksomhet == virksomhet}

    fun lagreValgtAktivitet(valgtAktivitet: ValgtAktivitet): ValgtAktivitet {
        valgteAktiviteter.add(valgtAktivitet)
        return valgtAktivitet
    }

    fun hentFullførteAktiviteterForVirksomhet(virksomhet: Virksomhet) =
        fullførteAktiviteter.filter { it.fullførtAv.virksomhet == virksomhet}

    // MOCK DATA INNTIL VI HAR DB PÅ PLASS
    private var aktivitetsmaler: List<Aktivitetsmal> = listOf(
        Aktivitetsmal(
            tittel = "Pilotering av medarbeidersamtalen"
        ),
        Aktivitetsmal(
            tittel = "Kartleggingsmøte med ansatt"
        ),
        Aktivitetsmal(
            tittel = "Sinnemestring"
        ),
        Aktivitetsmal(
            tittel = "Hvordan ta den vanskelige praten?"
        ),
    )
    private val virksomhet = Virksomhet("123456789")

    private val arbeidsgiverRepresentant = ArbeidsgiverRepresentant(fnr = "12345678912", virksomhet = virksomhet)
    private var valgteAktiviteter : MutableList<ValgtAktivitet> = mutableListOf()
    private var fullførteAktiviteter : MutableList<FullførtAktivitet> = mutableListOf()
    // SLUTT MOCK DATA

}
