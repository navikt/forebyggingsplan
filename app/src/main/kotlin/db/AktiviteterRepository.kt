package db

import domene.Aktivitet
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object AktiviteterRepository : Table("aktiviteter") {
    private val hashetFodselsnummer = binary("hashet_fodselsnummer", 64)
    private val orgnr = varchar("orgnr", 9)
    private val aktivitetsid = varchar("aktivitetsid", 45)
    private val aktivitetsversjon = varchar("aktivitetsversjon", 45)
    private val fullført = bool("fullfort")
    private val fullføringstidspunkt = timestamp("fullforingstidspunkt").nullable()
    override val primaryKey = PrimaryKey(hashetFodselsnummer, orgnr, aktivitetsid)

    fun settAktivitet(aktivitet: Aktivitet) {
        settAktivitet(AktivitetDto(aktivitet))
    }

    fun hentAlleFullførteAktiviteterFor(hashetFødselsnummerQuery: ByteArray, orgnrQuery: String): List<Aktivitet> {
        return transaction {
            select {
                (hashetFodselsnummer eq hashetFødselsnummerQuery) and
                        (orgnr eq orgnrQuery) and
                        (fullført eq true)
            }.map(::tilDomene)
        }
    }

    private fun settAktivitet(aktivitetDto: AktivitetDto) {
        transaction {
            upsert {
                it[hashetFodselsnummer] = aktivitetDto.hashetFodselsnummer
                it[orgnr] = aktivitetDto.orgnr
                it[aktivitetsid] = aktivitetDto.aktivitetsid
                it[aktivitetsversjon] = aktivitetDto.aktivitetsversjon
                it[fullført] = aktivitetDto.fullført
                it[fullføringstidspunkt] = aktivitetDto.fullføringstidspunkt
            }
        }
    }

    fun tilDomene(it: ResultRow) = Aktivitet(
        hashetFodselsnummer = it[hashetFodselsnummer],
        orgnr = it[orgnr],
        aktivitetsid = it[aktivitetsid],
        aktivitetsversjon = it[aktivitetsversjon],
        fullført = it[fullført],
        fullføringstidspunkt = it[fullføringstidspunkt]?.toKotlinInstant(),
    )
}
