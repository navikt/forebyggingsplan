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
            tittel = "Pilotering av medarbeidersamtalen",
            beskrivelse = "Svada svada",
            type = AktivitetsType.Øvelse,
            mål = "Å bli ferdig"
        ),
        Aktivitet(
            tittel = "Kartleggingsmøte med ansatt",
            beskrivelse = "Enda mer svada",
            type = AktivitetsType.Oppgave,
            mål = "Å bli (nesten) ferdig"
        ),
        Aktivitet(
            tittel = "Sinnemestring",
            beskrivelse = "Hvordan ikke bli sinna?",
            type = AktivitetsType.Kurs,
            mål = "Ikke være sinna mer"
        ),
        Aktivitet(
            tittel = "Hvordan ta den vanskelige praten?",
            beskrivelse = "Vi hjelper deg på veien.",
            type = AktivitetsType.Kurs,
            mål = "Udefinert"
        ),
    )
    private val virksomhet = Virksomhet("123456789")

    private val arbeidsgiverRepresentant = ArbeidsgiverRepresentant(fnr = "12345678912", virksomhet = virksomhet)
    private var valgteAktiviteter : MutableList<ValgtAktivitet> = mutableListOf()
    private var fullførteAktiviteter : MutableList<FullførtAktivitet> = mutableListOf()
    // SLUTT MOCK DATA

}
