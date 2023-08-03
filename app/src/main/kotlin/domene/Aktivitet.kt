package domene

import api.dto.FullførtAktivitetDTO
import kotlinx.datetime.Instant

data class Aktivitet(
    val hashetFodselsnummer: ByteArray,
    val orgnr: String,
    val aktivitetsid: String,
    val aktivitetsversjon: String,
    val fullført: Boolean,
    val fullføringstidspunkt: Instant?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Aktivitet

        if (!hashetFodselsnummer.contentEquals(other.hashetFodselsnummer)) return false
        if (orgnr != other.orgnr) return false
        if (aktivitetsid != other.aktivitetsid) return false
        if (aktivitetsversjon != other.aktivitetsversjon) return false
        if (fullført != other.fullført) return false
        return fullføringstidspunkt == other.fullføringstidspunkt
    }

    override fun hashCode(): Int {
        var result = hashetFodselsnummer.contentHashCode()
        result = 31 * result + orgnr.hashCode()
        result = 31 * result + aktivitetsid.hashCode()
        result = 31 * result + aktivitetsversjon.hashCode()
        result = 31 * result + fullført.hashCode()
        result = 31 * result + (fullføringstidspunkt?.hashCode() ?: 0)
        return result
    }

    fun tilDto(): FullførtAktivitetDTO = FullførtAktivitetDTO(
        aktivitetsId = aktivitetsid,
        aktivitetsversjon = aktivitetsversjon,
        fullført = fullført,
        fullførtTidspunkt = fullføringstidspunkt,
    )
}
