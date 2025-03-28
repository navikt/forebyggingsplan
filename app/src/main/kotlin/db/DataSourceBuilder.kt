package db

import DbMiljø
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.postgresql.ds.PGSimpleDataSource
import javax.sql.DataSource

fun getFlyway(dataSource: DataSource): Flyway = Flyway.configure().validateMigrationNaming(true).dataSource(dataSource).load()

fun runMigration(dataSource: DataSource) {
    getFlyway(dataSource).migrate()
}

fun createDataSource(miljø: DbMiljø): DataSource =
    HikariDataSource().apply {
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
        maxLifetime = 300000
    }
