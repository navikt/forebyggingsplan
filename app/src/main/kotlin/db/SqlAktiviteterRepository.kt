package db

import domene.Aktivitet
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Clock
import java.time.ZoneOffset

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

    override fun settAktivitet(aktivitet: Aktivitet) {
        settAktivitet(AktivitetDto(aktivitet))
    }

    override fun hentAlleFullførteAktiviteterFor(hashetFnr: ByteArray, orgnr: String): List<Aktivitet.Aktivitetskort> {
        return transaction {
            select {
                (hashetFodselsnummer eq hashetFnr) and
                        (organisasjonsnummer eq orgnr) and
                        (fullført eq true)
            }.map(::tilDomene)
        }
    }

    override fun oppdaterOppgave(oppgave: Aktivitet.Oppgave) {
        settAktivitet(AktivitetDto(oppgave))
    }

    private fun settAktivitet(aktivitetDto: AktivitetDto) {
        transaction {
            upsert {
                Clock.system(ZoneOffset.UTC).instant()
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

    fun tilDomene(it: ResultRow) = Aktivitet.Aktivitetskort(
        hashetFodselsnummer = it[hashetFodselsnummer],
        orgnr = it[organisasjonsnummer],
        aktivitetsid = it[aktivitetsid],
        fullført = it[fullført]!!,
        fullføringstidspunkt = it[fullføringstidspunkt]?.toKotlinInstant(),
    )
}
