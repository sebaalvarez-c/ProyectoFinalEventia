package com.example.proyecto_final_eventia.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_eventia.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val success: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, error = null, fieldErrors = emptyMap())
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, error = null, fieldErrors = emptyMap())
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null, fieldErrors = emptyMap())
    }

    fun register() {
        val state = _uiState.value
        if (state.name.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Completá todos los campos")
            return
        }
        if (state.password.length < 8) {
            _uiState.value = state.copy(fieldErrors = mapOf("password" to "Mínimo 8 caracteres"))
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.register(state.name.trim(), state.email.trim(), state.password)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, success = true)
                }
                .onFailure { e ->
                    val (generalError, fieldErrors) = parseError(e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = generalError,
                        fieldErrors = fieldErrors
                    )
                }
        }
    }
}
