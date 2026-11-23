package com.example.proyecto_final_eventia.ui.venuedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_eventia.domain.model.Event
import com.example.proyecto_final_eventia.domain.model.Venue
import com.example.proyecto_final_eventia.data.repository.EventRepository
import com.example.proyecto_final_eventia.data.repository.VenueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VenueDetailUiState(
    val venue: Venue? = null,
    val upcomingEvents: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VenueDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val venueRepository: VenueRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val venueId: Int = checkNotNull(savedStateHandle["venueId"])

    private val _uiState = MutableStateFlow(VenueDetailUiState())
    val uiState: StateFlow<VenueDetailUiState> = _uiState.asStateFlow()

    init {
        loadVenue()
    }

    fun loadVenue() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            venueRepository.getVenueById(venueId)
                .onSuccess { venue ->
                    _uiState.value = _uiState.value.copy(venue = venue, isLoading = false)
                    loadVenueEvents()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se pudo cargar el lugar."
                    )
                }
        }
    }

    private fun loadVenueEvents() {
        viewModelScope.launch {
            venueRepository.getVenueEvents(venueId)
                .onSuccess { events ->
                    _uiState.value = _uiState.value.copy(upcomingEvents = events)
                }
        }
    }
}
