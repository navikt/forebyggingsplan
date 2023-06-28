package db

import DbMiljø
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.postgresql.ds.PGSimpleDataSource
import java.util.concurrent.TimeUnit.MINUTES

class DatabaseFactory(private val miljø: DbMiljø) {
    fun init() {
        Database.connect(hikari())
        val flyway = Flyway.configure().dataSource(hikari()).load()
        flyway.migrate()
    }

    private fun hikari(): HikariDataSource {
        return HikariDataSource().apply {
            dataSourceClassName = PGSimpleDataSource::class.qualifiedName
            addDataSourceProperty("serverName", miljø.dbHost)
            addDataSourceProperty("portNumber", miljø.dbPort)
            addDataSourceProperty("user", miljø.dbUser)
            addDataSourceProperty("password", miljø.dbPassword)
            addDataSourceProperty("databaseName", miljø.dbDatabaseName)
            maximumPoolSize = 10
            minimumIdle = 1
            idleTimeout = 100000
            connectionTimeout = 100000
            maxLifetime = MINUTES.toMillis(30)
        }
    }
}
