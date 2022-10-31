package domene

import api.dto.AktivitetsmalDTO
import java.util.*

class Aktivitetsmal(
    val id: String) {
    fun tilDto() = AktivitetsmalDTO(id = id)
}

