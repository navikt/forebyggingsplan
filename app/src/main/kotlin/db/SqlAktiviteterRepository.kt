package db

import domene.Aktivitet
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Clock

object SqlAktiviteterRepository : Table("aktiviteter"), AktiviteterRepository {
    private val hashetFodselsnummer = binary("hashet_fodselsnummer", 64)
    private val organisasjonsnummer = varchar("orgnr", 9)
    private val aktivitetsid = varchar("aktivitetsid", 45)
    private val aktivitetstype = enumerationByName<AktivitetDto.Aktivitetstype>("aktivitetstype", 45).nullable()
    private val sistEndret = timestamp("sist_endret").nullable()

    private val status = varchar("status", 45).nullable()
    private val fullført = bool("fullfort").nullable()
    private val fullføringstidspunkt = timestamp("fullforingstidspunkt").nullable()

    override val primaryKey = PrimaryKey(hashetFodselsnummer, organisasjonsnummer, aktivitetsid)

    override fun hentAktiviteter(hashetFnr: ByteArray, orgnr: String): List<Aktivitet> {
        return transaction {
            selectAll()
                .where {
                    (hashetFodselsnummer eq hashetFnr) and
                            (organisasjonsnummer eq orgnr)
                }
                .map(::tilDto)
                .mapNotNull(AktivitetDto::tilDomene)
        }
    }

    override fun oppdaterAktivitet(aktivitet: Aktivitet) {
        settAktivitet(AktivitetDto.fraDomene(aktivitet))
    }

    private fun tilDto(resultRow: ResultRow) = AktivitetDto(
        hashetFodselsnummer = resultRow[hashetFodselsnummer],
        orgnr = resultRow[organisasjonsnummer],
        aktivitetsid = resultRow[aktivitetsid],
        // Kun "aktivitetskort" kan ha aktivitetsype lik null
        aktivitetstype = resultRow[aktivitetstype] ?: AktivitetDto.Aktivitetstype.AKTIVITETSKORT,
        fullført = resultRow[fullført],
        fullføringstidspunkt = resultRow[fullføringstidspunkt],
        status = resultRow[status],
    )

    private fun settAktivitet(aktivitetDto: AktivitetDto) {
        transaction {
            upsert {
                it[hashetFodselsnummer] = aktivitetDto.hashetFodselsnummer
                it[organisasjonsnummer] = aktivitetDto.orgnr
                it[aktivitetsid] = aktivitetDto.aktivitetsid
                it[fullført] = aktivitetDto.fullført
                it[fullføringstidspunkt] = aktivitetDto.fullføringstidspunkt
                it[status] = aktivitetDto.status
                it[sistEndret] = Clock.systemUTC().instant()
                it[aktivitetstype] = aktivitetDto.aktivitetstype
            }
        }
    }
}
