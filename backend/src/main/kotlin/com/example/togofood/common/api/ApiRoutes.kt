package com.example.togofood.common.api

import io.ktor.server.application.call
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class ServiceInfo(val name: String, val version: String)

fun Route.apiRoutes() {
    route("/api/v1") {
        get {
            call.respond(ServiceInfo(name = "TogoFood API", version = "v1"))
        }

        swaggerUI(path = "docs", swaggerFile = "openapi/openapi.yaml")
    }
}
