package com.example.proyectofinaleventia.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.proyectofinaleventia.R
import kotlinx.serialization.Serializable

@Serializable
data object ExploreRoute

@Serializable
data object MapRoute

@Serializable
data object FavoritesRoute

@Serializable
data object ProfileRoute

enum class TopLevelDestination(
    val route: Any,
    val icon: ImageVector,
    @param:StringRes val labelRes: Int
) {
    EXPLORE(ExploreRoute, Icons.Filled.Search, R.string.nav_explore),
    MAP(MapRoute, Icons.Filled.Place, R.string.nav_map),
    FAVORITES(FavoritesRoute, Icons.Filled.Favorite, R.string.nav_favorites),
    PROFILE(ProfileRoute, Icons.Filled.Person, R.string.nav_profile)
}
