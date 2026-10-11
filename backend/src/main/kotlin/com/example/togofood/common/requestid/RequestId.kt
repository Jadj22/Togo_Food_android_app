package com.example.togofood.common.requestid

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import java.util.UUID

fun Application.installRequestId() {
    install(CallId) {
        generate { UUID.randomUUID().toString() }
        header(HttpHeaders.XRequestId)
        verify { it.isNotBlank() && it.length <= 128 }
    }
}
