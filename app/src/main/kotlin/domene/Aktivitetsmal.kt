package domene

import api.dto.AktivitetsmalDTO
import java.util.*

class Aktivitetsmal(
    val id: UUID = UUID.randomUUID(),
    private val tittel: String = ""
) {
    fun tilDto() = AktivitetsmalDTO(id = id.toString(), tittel = tittel)
}

