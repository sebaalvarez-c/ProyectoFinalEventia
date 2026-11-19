package com.example.proyecto_final_eventia.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.proyecto_final_eventia.domain.model.Venue
import com.example.proyecto_final_eventia.ui.components.ErrorContent
import com.example.proyecto_final_eventia.ui.components.LoadingContent
import androidx.compose.ui.draw.clipToBounds
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// ESRI World Street Map — free, no API key required.
// ESRI uses {z}/{y}/{x} order, so we override getTileURLString to swap y and x.
private val MAP_TILES = object : XYTileSource(
    "ESRI", 0, 18, 256, "",
    arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/")
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val z = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return "${baseUrl}${z}/${y}/${x}"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onVenueClick: (Int) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()

    LaunchedEffect(state.selectedVenue) {
        if (state.selectedVenue != null) {
            scaffoldState.bottomSheetState.expand()
        } else {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(title = { Text("Mapa") }) },
        sheetPeekHeight = 0.dp,
        sheetContent = {
            state.selectedVenue?.let { venue ->
                VenueBottomSheet(venue = venue, onViewDetail = { onVenueClick(venue.id) })
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.selectedCategory == null,
                        onClick = { viewModel.onCategorySelected(null) },
                        label = { Text("Todos") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                if (state.categories.isNotEmpty()) {
                    items(state.categories) { category ->
                        FilterChip(
                            selected = state.selectedCategory?.id == category.id,
                            onClick = {
                                viewModel.onCategorySelected(
                                    if (state.selectedCategory?.id == category.id) null else category
                                )
                            },
                            label = { Text(category.name) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when {
                    state.isLoading -> LoadingContent()
                    state.error != null -> ErrorContent(message = state.error!!, onRetry = viewModel::loadData)
                    else -> OsmdroidMapView(
                        venues = state.filteredVenues,
                        onVenueClick = { viewModel.onVenueSelected(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OsmdroidMapView(
    venues: List<Venue>,
    onVenueClick: (Venue) -> Unit
) {
    val mapView = remember { mutableListOf<MapView>() }

    AndroidView(
        factory = { ctx ->
            Configuration.getInstance().apply {
                userAgentValue = ctx.packageName
                osmdroidBasePath = java.io.File(ctx.cacheDir, "osmdroid")
                osmdroidTileCache = java.io.File(ctx.cacheDir, "osmdroid/tiles")
            }
            MapView(ctx).apply {
                setTileSource(MAP_TILES)
                setMultiTouchControls(true)
                controller.setZoom(13.0)
                if (venues.isNotEmpty()) {
                    val first = venues.first()
                    controller.setCenter(GeoPoint(first.lat!!, first.lng!!))
                } else {
                    controller.setCenter(GeoPoint(-34.6037, -58.3816))
                }
                mapView.add(this)
            }
        },
        update = { map ->
            map.overlays.clear()
            venues.forEach { venue ->
                if (venue.lat != null && venue.lng != null) {
                    val marker = Marker(map).apply {
                        position = GeoPoint(venue.lat, venue.lng)
                        title = venue.name
                        snippet = venue.address
                        setOnMarkerClickListener { _, _ ->
                            onVenueClick(venue)
                            true
                        }
                    }
                    map.overlays.add(marker)
                }
            }
            map.invalidate()
        },
        modifier = Modifier.fillMaxSize().clipToBounds()
    )
}

@Composable
private fun VenueBottomSheet(venue: Venue, onViewDetail: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(venue.name, style = MaterialTheme.typography.titleMedium)
        Text(
            venue.address,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (venue.upcomingEventsCount != null && venue.upcomingEventsCount > 0) {
            Text(
                "${venue.upcomingEventsCount} evento(s) próximo(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        androidx.compose.material3.TextButton(onClick = onViewDetail) {
            Text("Ver detalle")
        }
    }
}
