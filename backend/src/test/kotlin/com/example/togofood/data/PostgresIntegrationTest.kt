package com.example.togofood.data

import com.example.togofood.common.health.healthRoutes
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.Connection
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PostgresIntegrationTest {

    private companion object {
        const val DUPLICATE_KEY_SQLSTATE = "23505"

        val postgres: PostgreSQLContainer<Nothing> by lazy {
            PostgreSQLContainer<Nothing>("postgres:16-alpine").also { it.start() }
        }
    }

    private fun connect(): Connection = DatabaseFactory.initialize(
        DatabaseConfig(
            jdbcUrl = postgres.jdbcUrl,
            username = postgres.username,
            password = postgres.password,
            maximumPoolSize = 2,
        )
    ).connection

    private fun assumeDocker() {
        assumeTrue(
            DockerClientFactory.instance().isDockerAvailable,
            "Docker est requis pour les tests d'intégration PostgreSQL (Testcontainers) ; test ignoré.",
        )
    }

    @Test
    fun `migrations are applied on an empty database and the pool reports ready`() {
        assumeDocker()

        connect().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(
                    "SELECT count(*) FROM flyway_schema_history WHERE success"
                ).use { rows ->
                    assertTrue(rows.next())
                    assertTrue(rows.getInt(1) >= 1, "Au moins une migration doit être enregistrée")
                }

                statement.executeQuery("SELECT count(*) FROM zones").use { rows ->
                    assertTrue(rows.next(), "La table zones doit exister et être interrogeable")
                }
            }
        }

        assertTrue(DatabaseFactory.isReady(), "Le pool doit répondre SELECT 1 une fois la base prête")
    }

    @Test
    fun `zones stores UTF-8 by default and enforces uniqueness`() {
        assumeDocker()

        connect().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(
                    "INSERT INTO zones (name) VALUES ('Agoè-Nyivé') RETURNING city, is_active"
                ).use { rows ->
                    assertTrue(rows.next())
                    assertEquals("Lomé", rows.getString("city"), "Le défaut UTF-8 de city doit être préservé")
                    assertTrue(rows.getBoolean("is_active"))
                }
            }

            val duplicate = runCatching {
                connection.createStatement().use { statement ->
                    statement.executeUpdate(
                        "INSERT INTO zones (name, city) VALUES ('Agoè-Nyivé', 'Lomé')"
                    )
                }
            }.exceptionOrNull()

            assertTrue(duplicate is SQLException, "Le doublon (name, city) doit être refusé")
            assertEquals(DUPLICATE_KEY_SQLSTATE, (duplicate as SQLException).sqlState)
        }
    }

    @Test
    fun `readiness endpoint reports success once the database is reachable`() = testApplication {
        assumeDocker()
        DatabaseFactory.initialize(
            DatabaseConfig(
                jdbcUrl = postgres.jdbcUrl,
                username = postgres.username,
                password = postgres.password,
                maximumPoolSize = 2,
            )
        )

        application {
            routing {
                healthRoutes(DatabaseFactory::isReady)
            }
        }

        val response = client.get("/health/ready")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("ok"))
    }
}
