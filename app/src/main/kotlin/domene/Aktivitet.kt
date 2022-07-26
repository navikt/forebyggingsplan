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
