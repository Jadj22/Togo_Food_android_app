package com.example.togofood.common.api

import com.example.togofood.common.error.ApiError
import com.example.togofood.common.error.ErrorCodes
import com.example.togofood.common.error.installErrorHandling
import com.example.togofood.common.requestid.installRequestId
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApiRoutesTest {

    private fun Application.installTestStack() {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        installRequestId()
        installErrorHandling()
    }

    @Test
    fun `api base under version prefix responds with service info`() = testApplication {
        application {
            installTestStack()
            routing { apiRoutes() }
        }

        val response = client.get("/api/v1")

        assertEquals(HttpStatusCode.OK, response.status)
        val info = Json.decodeFromString<ServiceInfo>(response.bodyAsText())
        assertEquals("TogoFood API", info.name)
        assertEquals("v1", info.version)
    }

    @Test
    fun `unknown api route returns uniform not found payload`() = testApplication {
        application {
            installTestStack()
            routing { apiRoutes() }
        }

        val response = client.get("/api/v1/zones")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val error = Json.decodeFromString<ApiError>(response.bodyAsText())
        assertEquals(ErrorCodes.NOT_FOUND, error.code)
    }

    @Test
    fun `swagger ui is served under the api version prefix`() = testApplication {
        application {
            installTestStack()
            routing { apiRoutes() }
        }

        val response = client.get("/api/v1/docs")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("swagger", ignoreCase = true))
    }

    @Test
    fun `openapi specification is served next to the docs ui`() = testApplication {
        application {
            installTestStack()
            routing { apiRoutes() }
        }

        val response = client.get("/api/v1/docs/openapi.yaml")

        assertEquals(HttpStatusCode.OK, response.status)
        val spec = response.bodyAsText()
        assertTrue(spec.contains("openapi: 3.0.3"))
        assertTrue(spec.contains("/api/v1"))
    }
}
