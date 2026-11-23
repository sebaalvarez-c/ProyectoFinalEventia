package com.example.proyecto_final_eventia.ui.venuedetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyecto_final_eventia.ui.components.EmptyContent
import com.example.proyecto_final_eventia.ui.components.ErrorContent
import com.example.proyecto_final_eventia.ui.components.EventCard
import com.example.proyecto_final_eventia.ui.components.LoadingContent
import com.example.proyecto_final_eventia.ui.favorites.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueDetailScreen(
    onBack: () -> Unit,
    onEventClick: (Int) -> Unit,
    viewModel: VenueDetailViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.venue?.name ?: "Lugar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                state.isLoading -> LoadingContent()
                state.error != null -> ErrorContent(message = state.error!!, onRetry = viewModel::loadVenue)
                state.venue != null -> {
                    val venue = state.venue!!
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(venue.name, style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                venue.address,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (!venue.description.isNullOrBlank()) {
                                Spacer(Modifier.height(12.dp))
                                Text(venue.description, style = MaterialTheme.typography.bodyMedium)
                            }

                            if (!venue.openingHours.isNullOrBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Horario: ${venue.openingHours}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (venue.lat != null && venue.lng != null) {
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        val uri = Uri.parse("geo:${venue.lat},${venue.lng}?q=${venue.lat},${venue.lng}(${Uri.encode(venue.name)})")
                                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Cómo llegar") }
                            }

                            Spacer(Modifier.height(24.dp))

                            if (state.upcomingEvents.isNotEmpty()) {
                                Text(
                                    "Próximos eventos",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }

                        if (state.upcomingEvents.isEmpty() && !state.isLoading) {
                            item {
                                EmptyContent("No hay próximos eventos en este lugar.")
                            }
                        } else {
                            items(state.upcomingEvents, key = { it.id }) { event ->
                                EventCard(
                                    event = event,
                                    isFavorite = event.id in favoriteIds,
                                    onClick = { onEventClick(event.id) },
                                    onFavoriteToggle = { favoritesViewModel.toggleFavorite(event) }
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
