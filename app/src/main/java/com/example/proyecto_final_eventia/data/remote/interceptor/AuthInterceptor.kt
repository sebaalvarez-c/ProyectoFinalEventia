package com.example.proyecto_final_eventia.data.remote.interceptor

import com.example.proyecto_final_eventia.data.local.datastore.SessionDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { sessionDataStore.token.firstOrNull() }
        val request = chain.request().newBuilder().apply {
            addHeader("Accept", "application/json")
            if (token != null) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        val response = chain.proceed(request)
        if (response.code == 401) {
            runBlocking { sessionDataStore.clearSession() }
        }
        return response
    }
}
