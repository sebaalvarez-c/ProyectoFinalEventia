package com.example.proyecto_final_eventia.ui.auth

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException

data class ParsedError(
    val generalMessage: String?,
    val fieldErrors: Map<String, String>
)

fun parseError(throwable: Throwable): ParsedError {
    if (throwable !is HttpException) {
        return ParsedError("Sin conexión. Revisá tu internet.", emptyMap())
    }
    return try {
        val body = throwable.response()?.errorBody()?.string() ?: ""
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val map: Map<String, Any> = Gson().fromJson(body, type)
        val message = map["message"] as? String
        @Suppress("UNCHECKED_CAST")
        val rawFields = map["fields"] as? Map<String, Any> ?: emptyMap()
        val fields = rawFields.mapValues { it.value.toString() }
        ParsedError(generalMessage = message, fieldErrors = fields)
    } catch (e: Exception) {
        ParsedError("Error inesperado. Intentá de nuevo.", emptyMap())
    }
}
