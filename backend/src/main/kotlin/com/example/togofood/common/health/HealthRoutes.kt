package com.example.togofood.common.health

import io.ktor.http.HttpStatusCode
import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthRoutes(databaseReady: () -> Boolean) {
    get("/health/live") {
        call.respondText("""{"status":"ok"}""", ContentType.Application.Json)
    }

    get("/health/ready") {
        if (databaseReady()) {
            call.respondText("""{"status":"ok"}""", ContentType.Application.Json)
        } else {
            call.respondText(
                """{"status":"not_ready"}""",
                ContentType.Application.Json,
                HttpStatusCode.ServiceUnavailable
            )
        }
    }
}
