package com.example.togofood.common.error

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ErrorHandling")

fun Application.installErrorHandling() {
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            call.respond(
                cause.statusCode,
                ApiError(
                    code = cause.code,
                    message = cause.message,
                    requestId = call.callId,
                    details = cause.details,
                ),
            )
        }

        exception<Throwable> { call, cause ->
            logger.error("Unhandled exception [requestId={}]", call.callId, cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiError(
                    code = ErrorCodes.INTERNAL_ERROR,
                    message = "Une erreur interne est survenue.",
                    requestId = call.callId,
                ),
            )
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status,
                ApiError(
                    code = ErrorCodes.NOT_FOUND,
                    message = "Ressource introuvable.",
                    requestId = call.callId,
                ),
            )
        }
    }
}
