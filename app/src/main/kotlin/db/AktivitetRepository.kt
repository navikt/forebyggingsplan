package db

import domene.*
import domene.ForeslåttAktivitet.Companion.foreslåAktivitetForVirksomhet
import domene.ValgtAktivitet.Companion.velgForeslåttAktivitet

class AktivitetRepository {

    fun hentAktiviteter() = aktiviteter

    fun hentAktiviteterForVirksomhet(virksomhet: Virksomhet): List<Aktivitet> {
        return foreslåtteAktiviteter
            .filter { foreslåttAktivitet -> foreslåttAktivitet.foreslåttFor == virksomhet }
            .map { it.aktivitet }
    }

    fun lagreValgtAktivitet(valgAktivitet: ValgtAktivitet): ValgtAktivitet {
        valgteAktiviteter.add(valgAktivitet)
        return valgAktivitet
    }

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
    private var foreslåtteAktiviteter = aktiviteter.subList(0, 2).map { aktivitet ->
        foreslåAktivitetForVirksomhet(aktivitet = aktivitet, virksomhet = virksomhet)
    }

    private val arbeidsgiverRepresentant = ArbeidsgiverRepresentant(fnr = "12345678912", virksomhet = virksomhet)
    private var valgteAktiviteter : MutableList<ValgtAktivitet> = mutableListOf(arbeidsgiverRepresentant.velgForeslåttAktivitet(foreslåtteAktiviteter.first()))
    // SLUTT MOCK DATA




}
