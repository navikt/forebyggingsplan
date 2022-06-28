package domene

class Aktivitet(
    private val tittel: String,
    private val beskrivelse: String,
    private val type: AktivitetsType,
    private val mål: String
) {
    enum class AktivitetsType { Øvelse, Kurs, Oppgave }
}

