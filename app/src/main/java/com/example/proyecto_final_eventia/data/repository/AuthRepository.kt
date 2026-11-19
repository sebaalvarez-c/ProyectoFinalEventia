package com.example.proyecto_final_eventia.data.repository

import com.example.proyecto_final_eventia.data.local.datastore.SessionDataStore
import com.example.proyecto_final_eventia.data.remote.api.EventiaApi
import com.example.proyecto_final_eventia.data.remote.dto.LoginRequestDto
import com.example.proyecto_final_eventia.data.remote.dto.RegisterRequestDto
import com.example.proyecto_final_eventia.data.remote.dto.toDomain
import com.example.proyecto_final_eventia.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: EventiaApi,
    private val sessionDataStore: SessionDataStore
) {

    val token: Flow<String?> = sessionDataStore.token

    val currentUser: Flow<User?> = sessionDataStore.currentUser

    suspend fun login(email: String, password: String): Result<User> = runCatching {
        val response = api.login(LoginRequestDto(email, password))
        val user = response.user.toDomain()
        sessionDataStore.saveSession(response.token, user)
        user
    }

    suspend fun register(name: String, email: String, password: String): Result<User> =
        runCatching {
            val response = api.register(RegisterRequestDto(name, email, password))
            val user = response.user.toDomain()
            sessionDataStore.saveSession(response.token, user)
            user
        }

    suspend fun logout() {
        sessionDataStore.clearSession()
    }

    suspend fun getMe(): Result<User> = runCatching {
        api.getMe().toDomain()
    }
}
