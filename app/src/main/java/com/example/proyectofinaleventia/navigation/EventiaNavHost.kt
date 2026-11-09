package com.example.proyectofinaleventia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectofinaleventia.feature.explore.ExploreScreen
import com.example.proyectofinaleventia.feature.favorites.FavoritesScreen
import com.example.proyectofinaleventia.feature.map.MapScreen
import com.example.proyectofinaleventia.feature.profile.ProfileScreen

@Composable
fun EventiaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ExploreRoute,
        modifier = modifier
    ) {
        composable<ExploreRoute> { ExploreScreen() }
        composable<MapRoute> { MapScreen() }
        composable<FavoritesRoute> { FavoritesScreen() }
        composable<ProfileRoute> { ProfileScreen() }
    }
}
