package com.example.proyecto_final_eventia.data.local.datastore

import android.content.Context
import android.content.SharedPreferences
import com.example.proyecto_final_eventia.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("eventia_session", Context.MODE_PRIVATE)

    val token: Flow<String?> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(prefs.getString(KEY_TOKEN, null))
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        send(prefs.getString(KEY_TOKEN, null))
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    val currentUser: Flow<User?> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(readUser())
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        send(readUser())
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private fun readUser(): User? {
        val id = prefs.getInt(KEY_USER_ID, -1).takeIf { it != -1 } ?: return null
        val name = prefs.getString(KEY_USER_NAME, null) ?: return null
        val email = prefs.getString(KEY_USER_EMAIL, null) ?: return null
        return User(id, name, email)
    }

    suspend fun saveSession(token: String, user: User) {
        withContext(Dispatchers.IO) {
            prefs.edit()
                .putString(KEY_TOKEN, token)
                .putInt(KEY_USER_ID, user.id)
                .putString(KEY_USER_NAME, user.name)
                .putString(KEY_USER_EMAIL, user.email)
                .apply()
        }
    }

    suspend fun clearSession() {
        withContext(Dispatchers.IO) {
            prefs.edit().clear().apply()
        }
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
    }
}
