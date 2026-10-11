package com.example.togofood

import com.example.togofood.common.api.ServiceInfo
import com.example.togofood.common.api.apiRoutes
import com.example.togofood.common.error.installErrorHandling
import com.example.togofood.common.health.healthRoutes
import com.example.togofood.common.requestid.installRequestId
import com.example.togofood.data.DatabaseFactory
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, host = "0.0.0.0", port = port, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val dataSource = DatabaseFactory.initialize()
    monitor.subscribe(ApplicationStopped) {
        dataSource.close()
    }

    installRequestId()
    install(CallLogging) {
        mdc("requestId") { call -> call.callId ?: "" }
    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
    installErrorHandling()

    routing {
        healthRoutes(DatabaseFactory::isReady)
        get("/") {
            call.respond(ServiceInfo(name = "TogoFood API", version = "v1"))
        }
        apiRoutes()
    }
}
