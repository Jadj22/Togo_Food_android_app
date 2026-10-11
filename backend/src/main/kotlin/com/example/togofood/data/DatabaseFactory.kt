package com.example.togofood.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import java.sql.SQLException

object DatabaseFactory {
    @Volatile
    private var dataSource: HikariDataSource? = null

    fun initialize(config: DatabaseConfig = DatabaseConfig.fromEnvironment()): HikariDataSource {
        dataSource?.let { return it }
        return synchronized(this) {
            dataSource ?: createDataSource(config).also { pool ->
                try {
                    Flyway.configure()
                        .dataSource(pool)
                        .locations("classpath:db/migration")
                        .load()
                        .migrate()
                    dataSource = pool
                } catch (exception: Exception) {
                    pool.close()
                    throw exception
                }
            }
        }
    }

    fun isReady(): Boolean {
        val pool = dataSource ?: return false
        return try {
            pool.connection.use { connection ->
                connection.createStatement().use { statement ->
                    statement.execute("SELECT 1")
                }
            }
        } catch (_: SQLException) {
            false
        }
    }

    private fun createDataSource(config: DatabaseConfig): HikariDataSource {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.jdbcUrl
            username = config.username
            password = config.password
            maximumPoolSize = config.maximumPoolSize
            poolName = "togofood-postgres"
        }
        return HikariDataSource(hikariConfig)
    }
}

data class DatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val maximumPoolSize: Int,
) {
    companion object {
        fun fromEnvironment(): DatabaseConfig = DatabaseConfig(
            jdbcUrl = System.getenv("DATABASE_URL")
                ?: "jdbc:postgresql://localhost:5433/togofood",
            username = System.getenv("DATABASE_USER") ?: "togofood",
            password = System.getenv("DATABASE_PASSWORD") ?: "togofood",
            maximumPoolSize = System.getenv("DATABASE_POOL_SIZE")?.toIntOrNull() ?: 10,
        )
    }
}
