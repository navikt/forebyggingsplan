package domene

import kotlinx.datetime.Instant

sealed class Aktivitet {
    abstract val hashetFodselsnummer: ByteArray
    abstract val orgnr: String
    abstract val aktivitetsid: String


    data class Aktivitetskort(
        override val hashetFodselsnummer: ByteArray,
        override val orgnr: String,
        override val aktivitetsid: String,
        val fullført: Boolean,
        val fullføringstidspunkt: Instant?,
    ) : Aktivitet() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Aktivitetskort

            if (!hashetFodselsnummer.contentEquals(other.hashetFodselsnummer)) return false
            if (orgnr != other.orgnr) return false
            if (aktivitetsid != other.aktivitetsid) return false
            if (fullført != other.fullført) return false
            if (fullføringstidspunkt != other.fullføringstidspunkt) return false

            return true
        }

        override fun hashCode(): Int {
            var result = hashetFodselsnummer.contentHashCode()
            result = 31 * result + orgnr.hashCode()
            result = 31 * result + aktivitetsid.hashCode()
            result = 31 * result + fullført.hashCode()
            result = 31 * result + (fullføringstidspunkt?.hashCode() ?: 0)
            return result
        }
    }

    data class Oppgave(
        override val hashetFodselsnummer: ByteArray,
        override val orgnr: String,
        override val aktivitetsid: String,
        val status: Status,
    ) : Aktivitet() {

        enum class Status {
            STARTET, FULLFØRT, AVBRUTT
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Oppgave

            if (!hashetFodselsnummer.contentEquals(other.hashetFodselsnummer)) return false
            if (orgnr != other.orgnr) return false
            if (aktivitetsid != other.aktivitetsid) return false
            if (status != other.status) return false

            return true
        }

        override fun hashCode(): Int {
            var result = hashetFodselsnummer.contentHashCode()
            result = 31 * result + orgnr.hashCode()
            result = 31 * result + aktivitetsid.hashCode()
            result = 31 * result + status.hashCode()
            return result
        }
    }
}
