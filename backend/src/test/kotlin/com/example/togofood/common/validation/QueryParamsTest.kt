package com.example.togofood.common.validation

import com.example.togofood.common.error.ApiError
import com.example.togofood.common.error.ErrorCodes
import com.example.togofood.common.error.FieldError
import com.example.togofood.common.error.installErrorHandling
import com.example.togofood.common.requestid.installRequestId
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
private data class EchoParams(
    val limit: Int,
    val open: Boolean,
    val q: String? = null,
    val cursor: String? = null,
    val minPrice: Int? = null,
)

class QueryParamsTest {

    private fun Application.installTestStack() {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        installRequestId()
        installErrorHandling()
        routing {
            get("/things") {
                call.respond(
                    EchoParams(
                        limit = call.limitParam(),
                        open = call.queryBoolean("open", default = false),
                        q = call.queryText("q", maxLength = 100),
                        cursor = call.cursorParam(),
                        minPrice = call.queryOptionalInt("minPrice", min = 0, max = 1_000_000),
                    ),
                )
            }
        }
    }

    private suspend fun io.ktor.client.HttpClient.getParams(url: String): EchoParams =
        Json.decodeFromString(get(url).bodyAsText())

    private suspend fun io.ktor.client.HttpClient.getError(url: String): ApiError =
        Json.decodeFromString(get(url).bodyAsText())

    @Test
    fun `missing parameters fall back to defaults`() = testApplication {
        application { installTestStack() }

        val params = client.getParams("/things")

        assertEquals(20, params.limit)
        assertEquals(false, params.open)
        assertEquals(null, params.q)
        assertEquals(null, params.cursor)
        assertEquals(null, params.minPrice)
    }

    @Test
    fun `valid parameters are parsed`() = testApplication {
        application { installTestStack() }

        val params = client.getParams("/things?limit=5&open=true&q=poisson&cursor=abc&minPrice=100")

        assertEquals(5, params.limit)
        assertEquals(true, params.open)
        assertEquals("poisson", params.q)
        assertEquals("abc", params.cursor)
        assertEquals(100, params.minPrice)
    }

    @Test
    fun `non numeric limit returns 400 invalid format`() = testApplication {
        application { installTestStack() }

        val response = client.get("/things?limit=abc")
        val error = client.getError("/things?limit=abc")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(ErrorCodes.VALIDATION_ERROR, error.code)
        assertEquals(listOf(FieldError("limit", "invalid_format")), error.details)
    }

    @Test
    fun `limit out of range returns 400 with out of range reason`() = testApplication {
        application { installTestStack() }

        val error = client.getError("/things?limit=0")

        assertEquals(listOf(FieldError("limit", "out_of_range")), error.details)
    }

    @Test
    fun `repeated parameter returns 400 duplicate parameter`() = testApplication {
        application { installTestStack() }

        val error = client.getError("/things?limit=1&limit=2")

        assertEquals(listOf(FieldError("limit", "duplicate_parameter")), error.details)
    }

    @Test
    fun `non boolean open returns 400 invalid format`() = testApplication {
        application { installTestStack() }

        val error = client.getError("/things?open=maybe")

        assertEquals(listOf(FieldError("open", "invalid_format")), error.details)
    }

    @Test
    fun `q longer than max length returns 400 too long`() = testApplication {
        application { installTestStack() }

        val longQuery = "a".repeat(101)
        val error = client.getError("/things?q=$longQuery")

        assertEquals(listOf(FieldError("q", "too_long")), error.details)
    }

    @Test
    fun `negative min price returns 400 out of range`() = testApplication {
        application { installTestStack() }

        val error = client.getError("/things?minPrice=-1")

        assertEquals(listOf(FieldError("minPrice", "out_of_range")), error.details)
    }
}
