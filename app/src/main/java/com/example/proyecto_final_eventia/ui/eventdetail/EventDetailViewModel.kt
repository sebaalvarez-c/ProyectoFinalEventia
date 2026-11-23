package com.example.proyecto_final_eventia.ui.eventdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_eventia.domain.model.Event
import com.example.proyecto_final_eventia.data.repository.AuthRepository
import com.example.proyecto_final_eventia.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

data class EventDetailUiState(
    val event: Event? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val isAttending: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val attendanceMessage: String? = null
)

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val eventId: Int = checkNotNull(savedStateHandle["eventId"])

    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    init {
        checkAuth()
        loadEvent()
        observeFavorite()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            authRepository.token.collect { token ->
                _uiState.value = _uiState.value.copy(isLoggedIn = token != null)
            }
        }
    }

    private fun loadEvent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            eventRepository.getEventById(eventId)
                .onSuccess { event ->
                    _uiState.value = _uiState.value.copy(
                        event = event,
                        isLoading = false,
                        isAttending = event.isAttending == true
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se pudo cargar el evento."
                    )
                }
        }
    }

    private fun observeFavorite() {
        viewModelScope.launch {
            eventRepository.isFavorite(eventId).collect { fav ->
                _uiState.value = _uiState.value.copy(isFavorite = fav)
            }
        }
    }

    fun toggleFavorite() {
        val event = _uiState.value.event ?: return
        viewModelScope.launch { eventRepository.toggleFavorite(event) }
    }

    fun attendEvent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, attendanceMessage = null)
            eventRepository.attendEvent(eventId)
                .onSuccess { updated ->
                    _uiState.value = _uiState.value.copy(
                        event = updated,
                        isLoading = false,
                        isAttending = true,
                        attendanceMessage = "¡Inscripción confirmada!"
                    )
                }
                .onFailure { e ->
                    val message = when {
                        e is HttpException && e.code() == 409 -> "Ya estás inscripto en este evento."
                        e is HttpException && e.code() == 400 -> "No hay lugares disponibles."
                        else -> "No se pudo completar la inscripción."
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, attendanceMessage = message)
                }
        }
    }

    fun cancelAttendance() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, attendanceMessage = null)
            eventRepository.cancelAttendance(eventId)
                .onSuccess { updated ->
                    _uiState.value = _uiState.value.copy(
                        event = updated,
                        isLoading = false,
                        isAttending = false,
                        attendanceMessage = "Inscripción cancelada."
                    )
                }
                .onFailure { e ->
                    val message = when {
                        e is HttpException && e.code() == 409 -> "No estabas inscripto en este evento."
                        else -> "No se pudo cancelar la inscripción."
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, attendanceMessage = message)
                }
        }
    }

    fun clearAttendanceMessage() {
        _uiState.value = _uiState.value.copy(attendanceMessage = null)
    }
}
