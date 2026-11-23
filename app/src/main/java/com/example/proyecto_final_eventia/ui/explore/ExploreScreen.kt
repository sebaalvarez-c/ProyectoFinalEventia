package com.example.proyecto_final_eventia.ui.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyecto_final_eventia.ui.components.EmptyContent
import com.example.proyecto_final_eventia.ui.components.ErrorContent
import com.example.proyecto_final_eventia.ui.components.EventCard
import com.example.proyecto_final_eventia.ui.components.LoadingContent
import com.example.proyecto_final_eventia.ui.components.VenueCard
import com.example.proyecto_final_eventia.ui.favorites.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onEventClick: (Int) -> Unit,
    onVenueClick: (Int) -> Unit,
    viewModel: ExploreViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explorar") },
                actions = {
                    IconButton(onClick = viewModel::refreshEvents) {
                        Icon(Icons.Default.Refresh, "Actualizar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isOffline) {
                OfflineBanner(cachedAt = state.cachedAt)
            }

            TabRow(selectedTabIndex = state.selectedTab) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.onTabSelected(0) },
                    text = { Text("Eventos") }
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.onTabSelected(1) },
                    text = { Text("Lugares") }
                )
            }

            if (state.selectedTab == 0) {
                EventsTab(
                    state = state,
                    favoriteIds = favoriteIds,
                    onEventClick = onEventClick,
                    onFavoriteToggle = { favoritesViewModel.toggleFavorite(it) },
                    onQueryChange = viewModel::onSearchQueryChange,
                    onCategorySelected = viewModel::onCategorySelected,
                    onClearFilters = viewModel::clearFilters,
                    onRetry = viewModel::refreshEvents
                )
            } else {
                VenuesTab(
                    state = state,
                    onVenueClick = onVenueClick,
                    onRetry = viewModel::refreshEvents
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventsTab(
    state: ExploreUiState,
    favoriteIds: Set<Int>,
    onEventClick: (Int) -> Unit,
    onFavoriteToggle: (com.example.proyecto_final_eventia.domain.model.Event) -> Unit,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (com.example.proyecto_final_eventia.domain.model.Category?) -> Unit,
    onClearFilters: () -> Unit,
    onRetry: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("Buscar eventos...") },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, "Limpiar búsqueda")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (state.categories.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.height(48.dp)
            ) {
                items(state.categories) { category ->
                    FilterChip(
                        selected = state.selectedCategory?.id == category.id,
                        onClick = {
                            onCategorySelected(
                                if (state.selectedCategory?.id == category.id) null else category
                            )
                        },
                        label = { Text(category.name) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }

        val hasFilters = state.searchQuery.isNotEmpty() || state.selectedCategory != null
        if (hasFilters) {
            TextButton(
                onClick = onClearFilters,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) { Text("Limpiar filtros") }
        }

        when {
            state.isLoadingEvents -> LoadingContent()
            state.eventsError != null -> ErrorContent(
                message = state.eventsError,
                onRetry = onRetry
            )
            state.events.isEmpty() -> EmptyContent(
                message = if (hasFilters) "No hay eventos de \"${state.searchQuery}\" con esos filtros."
                else "No hay eventos disponibles.",
                actionLabel = if (hasFilters) "Limpiar filtros" else null,
                onAction = if (hasFilters) onClearFilters else null
            )
            else -> PullToRefreshBox(
                isRefreshing = state.isLoadingEvents,
                onRefresh = onRetry
            ) {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(state.events, key = { it.id }) { event ->
                        EventCard(
                            event = event,
                            isFavorite = event.id in favoriteIds,
                            onClick = { onEventClick(event.id) },
                            onFavoriteToggle = { onFavoriteToggle(event) }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun VenuesTab(
    state: ExploreUiState,
    onVenueClick: (Int) -> Unit,
    onRetry: () -> Unit
) {
    when {
        state.isLoadingVenues -> LoadingContent()
        state.venuesError != null -> ErrorContent(message = state.venuesError, onRetry = onRetry)
        state.venues.isEmpty() -> EmptyContent("No hay lugares disponibles.")
        else -> LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(state.venues, key = { it.id }) { venue ->
                VenueCard(venue = venue, onClick = { onVenueClick(venue.id) })
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun OfflineBanner(cachedAt: Long?) {
    val ageText = com.example.proyecto_final_eventia.util.formatCacheAge(cachedAt)
    androidx.compose.material3.Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sin conexión · $ageText",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}
