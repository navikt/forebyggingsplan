package domene

sealed class Aktivitet {
    abstract val hashetFodselsnummer: ByteArray
    abstract val orgnr: String
    abstract val aktivitetsid: String

    data class Teoriseksjon(
        override val hashetFodselsnummer: ByteArray,
        override val orgnr: String,
        override val aktivitetsid: String,
        val status: Status,
    ) : Aktivitet() {

        enum class Status {
            LEST, ULEST
        }

        override fun equals(other: Any?): Boolean {
            if (!super.equals(other)) {
                return false
            }
            return status == (other as Teoriseksjon).status
        }

        override fun hashCode(): Int {
            var result = hashetFodselsnummer.contentHashCode()
            result = 31 * result + orgnr.hashCode()
            result = 31 * result + aktivitetsid.hashCode()
            result = 31 * result + status.hashCode()
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
            if (!super.equals(other)) {
                return false
            }
            return status == (other as Oppgave).status
        }

        override fun hashCode(): Int {
            var result = hashetFodselsnummer.contentHashCode()
            result = 31 * result + orgnr.hashCode()
            result = 31 * result + aktivitetsid.hashCode()
            result = 31 * result + status.hashCode()
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Aktivitet

        if (!hashetFodselsnummer.contentEquals(other.hashetFodselsnummer)) return false
        if (orgnr != other.orgnr) return false
        if (aktivitetsid != other.aktivitetsid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hashetFodselsnummer.contentHashCode()
        result = 31 * result + orgnr.hashCode()
        result = 31 * result + aktivitetsid.hashCode()
        return result
    }
}
