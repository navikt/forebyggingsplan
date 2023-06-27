package db

import domene.Aktivitet
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import java.time.Instant

data class AktivitetDto(
    val hashetFodselsnummer: ByteArray,
    val orgnr: String,
    val aktivitetsid: String,
    val aktivitetsversjon: String,
    val fullført: Boolean? = null,
    val fullføringstidspunkt: Instant? = null
) {
    constructor(aktivitet: Aktivitet) : this(
        hashetFodselsnummer = aktivitet.hashetFodselsnummer,
        orgnr = aktivitet.orgnr,
        aktivitetsid = aktivitet.aktivitetsid,
        aktivitetsversjon = aktivitet.aktivitetsversjon,
        fullført = aktivitet.fullført,
        fullføringstidspunkt = aktivitet.fullføringstidspunkt?.toJavaInstant()
    )

    fun tilDomene() = Aktivitet(
        hashetFodselsnummer = hashetFodselsnummer,
        orgnr = orgnr,
        aktivitetsid = aktivitetsid,
        aktivitetsversjon = aktivitetsversjon,
        fullført = fullført,
        fullføringstidspunkt = fullføringstidspunkt?.toKotlinInstant()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AktivitetDto

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
        result = 31 * result + (fullført?.hashCode() ?: 0)
        result = 31 * result + (fullføringstidspunkt?.hashCode() ?: 0)
        return result
    }
}