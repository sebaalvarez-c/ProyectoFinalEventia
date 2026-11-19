package com.example.proyecto_final_eventia.data.remote.dto

import com.example.proyecto_final_eventia.domain.model.User

data class AuthResponseDto(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)

data class ApiErrorDto(
    val code: String,
    val message: String,
    val fields: Map<String, String>?
)

fun UserDto.toDomain() = User(id = id, name = name, email = email)
