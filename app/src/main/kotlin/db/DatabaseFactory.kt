package db

import Miljø
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.postgresql.ds.PGSimpleDataSource
import java.util.concurrent.TimeUnit.MINUTES

object DatabaseFactory {
    internal fun init() {
        Database.connect(hikari())
        val flyway = Flyway.configure().dataSource(hikari()).load()
        flyway.migrate()
    }

    private fun hikari(): HikariDataSource {
        return HikariDataSource().apply {
            dataSourceClassName = PGSimpleDataSource::class.qualifiedName
            addDataSourceProperty("serverName", Miljø.dbHost)
            addDataSourceProperty("portNumber", Miljø.dbPort)
            addDataSourceProperty("user", Miljø.dbUser)
            addDataSourceProperty("password", Miljø.dbPassword)
            addDataSourceProperty("databaseName", Miljø.dbDatabaseName)
            maximumPoolSize = 10
            minimumIdle = 1
            idleTimeout = 100000
            connectionTimeout = 100000
            maxLifetime = MINUTES.toMillis(30)
        }
    }
}
