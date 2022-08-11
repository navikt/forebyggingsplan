package domene

import api.dto.AktivitetDTO
import java.util.*

class Aktivitet(
    val id: String = "randomId-${(0..1000).random()}", // TODO: bruk ULID i stedet
    private val tittel: String
) {
    fun tilDto() = AktivitetDTO(id = id, tittel = tittel)
}

// TODO: bruk ULID i stedet
fun IntRange.random() =
    Random().nextInt((endInclusive + 1) - start) + start
