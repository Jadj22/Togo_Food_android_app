package com.example.togofood.common.error

import com.example.togofood.common.requestid.installRequestId
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.call
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ErrorHandlingTest {

    private fun Application.installTestStack() {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        installRequestId()
        installErrorHandling()
    }

    private fun Application.testRoutes() {
        routing {
            get("/zones/{id}") {
                throw NotFoundException("Zone", call.parameters["id"])
            }
            get("/conflict") {
                throw ConflictException("La zone existe déjà.")
            }
            get("/invalid") {
                throw ValidationException(
                    "La requête contient des champs invalides.",
                    listOf(FieldError("zoneId", "invalid_format")),
                )
            }
            get("/boom") {
                error("secret-db-password")
            }
        }
    }

    private fun parseError(body: String): ApiError = Json.decodeFromString(body)

    @Test
    fun `unknown path returns not found payload with request id`() = testApplication {
        application {
            installTestStack()
            testRoutes()
        }

        val response = client.get("/missing")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val error = parseError(response.bodyAsText())
        assertEquals(ErrorCodes.NOT_FOUND, error.code)
        assertNotNull(error.requestId)
        assertEquals(error.requestId, response.headers[HttpHeaders.XRequestId])
    }

    @Test
    fun `api exception keeps its status code and message`() = testApplication {
        application {
            installTestStack()
            testRoutes()
        }

        val response = client.get("/zones/abc")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val error = parseError(response.bodyAsText())
        assertEquals(ErrorCodes.NOT_FOUND, error.code)
        assertTrue(error.message.contains("abc"))
    }

    @Test
    fun `conflict exception returns 409 with conflict code`() = testApplication {
        application {
            installTestStack()
            testRoutes()
        }

        val response = client.get("/conflict")

        assertEquals(HttpStatusCode.Conflict, response.status)
        val error = parseError(response.bodyAsText())
        assertEquals(ErrorCodes.CONFLICT, error.code)
    }

    @Test
    fun `validation exception returns 400 with field details`() = testApplication {
        application {
            installTestStack()
            testRoutes()
        }

        val response = client.get("/invalid")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val error = parseError(response.bodyAsText())
        assertEquals(ErrorCodes.VALIDATION_ERROR, error.code)
        assertEquals(listOf(FieldError("zoneId", "invalid_format")), error.details)
    }

    @Test
    fun `unexpected exception returns 500 without leaking details`() = testApplication {
        application {
            installTestStack()
            testRoutes()
        }

        val response = client.get("/boom")

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        val error = parseError(response.bodyAsText())
        assertEquals(ErrorCodes.INTERNAL_ERROR, error.code)
        assertFalse(error.message.contains("secret"))
        assertNotNull(error.requestId)
    }

    @Test
    fun `incoming request id is reused in response header and payload`() = testApplication {
        application {
            installTestStack()
            testRoutes()
        }

        val response = client.get("/zones/abc") {
            header(HttpHeaders.XRequestId, "trace-123")
        }

        val error = parseError(response.bodyAsText())
        assertEquals("trace-123", error.requestId)
        assertEquals("trace-123", response.headers[HttpHeaders.XRequestId])
    }
}
