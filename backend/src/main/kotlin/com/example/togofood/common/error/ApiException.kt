package com.example.togofood.common.error

import io.ktor.http.HttpStatusCode

open class ApiException(
    val code: String,
    val statusCode: HttpStatusCode,
    override val message: String,
    val details: List<FieldError>? = null,
) : RuntimeException(message)

class ValidationException(
    message: String,
    details: List<FieldError>? = null,
) : ApiException(ErrorCodes.VALIDATION_ERROR, HttpStatusCode.BadRequest, message, details)

class UnauthorizedException(
    message: String = "Authentification requise.",
) : ApiException(ErrorCodes.UNAUTHORIZED, HttpStatusCode.Unauthorized, message)

class ForbiddenException(
    message: String = "Accès refusé.",
) : ApiException(ErrorCodes.FORBIDDEN, HttpStatusCode.Forbidden, message)

class NotFoundException(
    resource: String,
    id: String? = null,
) : ApiException(
    ErrorCodes.NOT_FOUND,
    HttpStatusCode.NotFound,
    if (id != null) "$resource $id introuvable." else "$resource introuvable.",
)

class ConflictException(
    message: String,
) : ApiException(ErrorCodes.CONFLICT, HttpStatusCode.Conflict, message)

class RateLimitedException(
    message: String = "Trop de requêtes. Réessayez plus tard.",
) : ApiException(ErrorCodes.RATE_LIMITED, HttpStatusCode.TooManyRequests, message)
