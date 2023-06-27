package db

import domene.Aktivitet
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp

object AktiviteterRepository : Table("aktiviteter") {
    private val hashetFodselsnummer = binary("hashet_fodselsnummer", 64)
    private val orgnr = varchar("orgnr", 9)
    private val aktivitetsid = varchar("aktivitetsid", 45)
    private val aktivitetsversjon = varchar("aktivitetsversjon", 45)
    private val fullført = bool("fullfoert").nullable()
    private val fullføringstidspunkt = timestamp("fullfoeringstidspunkt").nullable()
    override val primaryKey = PrimaryKey(hashetFodselsnummer, aktivitetsid, aktivitetsversjon)

    fun settAktivitet(aktivitet: Aktivitet) {
        settAktivitet(AktivitetDto(aktivitet))
    }

    fun hentAktivitet(aktivitetsnøkkel: Aktivitetsnøkkel): List<Aktivitet> {
        return select {
            (hashetFodselsnummer eq aktivitetsnøkkel.hashetFødselsnummer) and
                    (aktivitetsid eq aktivitetsnøkkel.aktivitetsid) and
                    (aktivitetsversjon eq aktivitetsnøkkel.aktivitetsversjon)
        }.map(::tilAktivitetDto).map(AktivitetDto::tilDomene)
    }

    private fun settAktivitet(aktivitetDto: AktivitetDto) {
        insertIgnore {
            it[hashetFodselsnummer] = aktivitetDto.hashetFodselsnummer
            it[orgnr] = aktivitetDto.orgnr
            it[aktivitetsid] = aktivitetDto.aktivitetsid
            it[aktivitetsversjon] = aktivitetDto.aktivitetsversjon
            it[fullført] = aktivitetDto.fullført
            it[fullføringstidspunkt] = aktivitetDto.fullføringstidspunkt
        }
    }

    private fun tilAktivitetDto(it: ResultRow) = AktivitetDto(
        hashetFodselsnummer = it[hashetFodselsnummer],
        orgnr = it[orgnr],
        aktivitetsid = it[aktivitetsid],
        aktivitetsversjon = it[aktivitetsversjon],
        fullført = it[fullført],
        fullføringstidspunkt = it[fullføringstidspunkt],
    )
}

interface Aktivitetsnøkkel {
    val hashetFødselsnummer: ByteArray
    val aktivitetsid: String
    val aktivitetsversjon: String
}

