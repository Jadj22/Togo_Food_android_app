package com.example.togofood.common.health

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.ktor.server.routing.routing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HealthRoutesTest {
    @Test
    fun `liveness endpoint returns success without database check`() = testApplication {
        application {
            routing {
                healthRoutes { error("Liveness must not query the database") }
            }
        }

        val response = client.get("/health/live")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("ok"))
    }

    @Test
    fun `readiness endpoint reports unavailable when database is unavailable`() = testApplication {
        application {
            routing {
                healthRoutes { false }
            }
        }

        val response = client.get("/health/ready")

        assertEquals(HttpStatusCode.ServiceUnavailable, response.status)
        assertTrue(response.bodyAsText().contains("not_ready"))
    }
}
