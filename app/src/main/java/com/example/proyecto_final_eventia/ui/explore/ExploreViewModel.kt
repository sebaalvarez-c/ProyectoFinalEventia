package com.example.proyecto_final_eventia.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_final_eventia.domain.model.Category
import com.example.proyecto_final_eventia.domain.model.Event
import com.example.proyecto_final_eventia.domain.model.Venue
import com.example.proyecto_final_eventia.data.repository.EventRepository
import com.example.proyecto_final_eventia.data.repository.VenueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val events: List<Event> = emptyList(),
    val venues: List<Venue> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val searchQuery: String = "",
    val selectedTab: Int = 0, // 0=Eventos, 1=Lugares
    val isLoadingEvents: Boolean = false,
    val isLoadingVenues: Boolean = false,
    val eventsError: String? = null,
    val venuesError: String? = null,
    val isOffline: Boolean = false,
    val cachedAt: Long? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val venueRepository: VenueRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")
    private val selectedCategoryFlow = MutableStateFlow<Category?>(null)

    init {
        loadCategories()
        loadVenues()
        observeEventFilters()
    }

    private fun observeEventFilters() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    fetchEvents(query, selectedCategoryFlow.value?.id)
                }
        }
    }

    fun onTabSelected(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchQueryFlow.value = query
    }

    fun onCategorySelected(category: Category?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        selectedCategoryFlow.value = category
        viewModelScope.launch {
            fetchEvents(searchQueryFlow.value, category?.id)
        }
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(searchQuery = "", selectedCategory = null)
        searchQueryFlow.value = ""
        selectedCategoryFlow.value = null
        viewModelScope.launch { fetchEvents(null, null) }
    }

    fun refreshEvents() {
        viewModelScope.launch {
            fetchEvents(searchQueryFlow.value, selectedCategoryFlow.value?.id)
        }
    }

    private suspend fun fetchEvents(query: String?, categoryId: Int?) {
        _uiState.value = _uiState.value.copy(isLoadingEvents = true, eventsError = null)
        try {
            eventRepository.getEvents(
                query = query?.takeIf { it.isNotBlank() },
                categoryId = categoryId
            ).collect { result ->
                _uiState.value = _uiState.value.copy(
                    events = result.events,
                    isLoadingEvents = false,
                    isOffline = result.isOffline,
                    cachedAt = result.cachedAt,
                    eventsError = null
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoadingEvents = false,
                eventsError = "No pudimos cargar los eventos. Revisá tu conexión."
            )
        }
    }

    private fun loadVenues() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingVenues = true)
            venueRepository.getVenues()
                .onSuccess { venues ->
                    _uiState.value = _uiState.value.copy(venues = venues, isLoadingVenues = false)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoadingVenues = false,
                        venuesError = "No pudimos cargar los lugares."
                    )
                }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            eventRepository.getCategories()
                .onSuccess { cats ->
                    _uiState.value = _uiState.value.copy(categories = cats)
                }
        }
    }
}
