package domene

import api.dto.AktivitetDTO

enum class AktivitetsType { Øvelse, Kurs, Oppgave }

class Aktivitet(
    private val tittel: String,
    private val beskrivelse: String,
    private val type: AktivitetsType,
    private val mål: String
) {
    fun tilDto() = AktivitetDTO(tittel = tittel, beskrivelse = beskrivelse, type = type, mål = mål)
}

val aktiviteter: List<Aktivitet> = listOf(
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

