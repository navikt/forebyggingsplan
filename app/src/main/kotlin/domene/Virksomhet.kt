package domene

class Virksomhet(
    val orgnr: String,
) {
    override fun equals(other: Any?) = this === other || other is Virksomhet && this.orgnr == other.orgnr

    override fun hashCode() = orgnr.hashCode()
}
