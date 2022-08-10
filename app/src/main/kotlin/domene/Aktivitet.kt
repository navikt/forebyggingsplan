package domene

import api.dto.AktivitetDTO
import java.util.*

enum class AktivitetsType { Øvelse, Kurs, Oppgave }

class Aktivitet(
    val id: String = "randomId-${(0..1000).random()}", // TODO: bruk ULID i stedet
    private val tittel: String,
    private val beskrivelse: String,
    private val type: AktivitetsType,
    private val mål: String
) {
    fun tilDto() = AktivitetDTO(id = id, tittel = tittel, beskrivelse = beskrivelse, type = type, mål = mål)
}

// TODO: bruk ULID i stedet
fun IntRange.random() =
    Random().nextInt((endInclusive + 1) - start) + start
