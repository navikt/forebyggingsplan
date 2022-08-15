package domene

import api.dto.AktivitetsmalDTO
import java.util.*

class Aktivitetsmal(
    val id: String = "randomId-${(0..1000).random()}", // TODO: bruk ULID i stedet
    private val tittel: String
) {
    fun tilDto() = AktivitetsmalDTO(id = id, tittel = tittel)
}

// TODO: bruk ULID i stedet
fun IntRange.random() =
    Random().nextInt((endInclusive + 1) - start) + start
