package com.example.togofood.common.validation

import com.example.togofood.common.error.FieldError
import com.example.togofood.common.error.ValidationException
import io.ktor.server.application.ApplicationCall

private const val INVALID_PAYLOAD_MESSAGE = "La requête contient des champs invalides."

private fun invalid(field: String, reason: String): Nothing =
    throw ValidationException(
        INVALID_PAYLOAD_MESSAGE,
        listOf(FieldError(field, reason)),
    )

private fun singleValue(call: ApplicationCall, name: String): String? {
    val values = call.request.queryParameters.getAll(name)
    if (values != null && values.size > 1) {
        invalid(name, "duplicate_parameter")
    }
    return values?.firstOrNull()
}

fun ApplicationCall.queryInt(name: String, default: Int, min: Int, max: Int): Int {
    val raw = singleValue(this, name) ?: return default
    val value = raw.toIntOrNull() ?: invalid(name, "invalid_format")
    if (value < min || value > max) invalid(name, "out_of_range")
    return value
}

fun ApplicationCall.queryOptionalInt(name: String, min: Int, max: Int): Int? {
    val raw = singleValue(this, name) ?: return null
    val value = raw.toIntOrNull() ?: invalid(name, "invalid_format")
    if (value < min || value > max) invalid(name, "out_of_range")
    return value
}

fun ApplicationCall.queryBoolean(name: String, default: Boolean): Boolean {
    val raw = singleValue(this, name)?.lowercase() ?: return default
    return when (raw) {
        "true" -> true
        "false" -> false
        else -> invalid(name, "invalid_format")
    }
}

fun ApplicationCall.queryText(name: String, maxLength: Int, required: Boolean = false): String? {
    val raw = singleValue(this, name)?.trim()
    if (raw.isNullOrEmpty()) {
        if (required) invalid(name, "required")
        return null
    }
    if (raw.length > maxLength) invalid(name, "too_long")
    return raw
}

fun ApplicationCall.limitParam(default: Int = 20, max: Int = 50): Int =
    queryInt("limit", default = default, min = 1, max = max)

fun ApplicationCall.cursorParam(): String? = queryText("cursor", maxLength = 256)
