package com.example.proyecto_final_eventia.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto_final_eventia.ui.auth.LoginScreen
import com.example.proyecto_final_eventia.ui.auth.RegisterScreen
import com.example.proyecto_final_eventia.ui.splash.SplashScreen
import com.example.proyecto_final_eventia.ui.eventdetail.EventDetailScreen
import com.example.proyecto_final_eventia.ui.explore.ExploreScreen
import com.example.proyecto_final_eventia.ui.favorites.FavoritesScreen
import com.example.proyecto_final_eventia.ui.map.MapScreen
import com.example.proyecto_final_eventia.ui.profile.ProfileScreen
import com.example.proyecto_final_eventia.ui.venuedetail.VenueDetailScreen

private data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: @Composable () -> Unit
)

@Composable
fun EventiaNavHost(token: String? = null) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var previousToken by remember { mutableStateOf<String?>(token) }
    LaunchedEffect(token) {
        if (previousToken != null && token == null) {
            navController.navigate(Screen.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
        previousToken = token
    }

    val bottomNavItems = listOf(
        BottomNavItem("Explorar", Screen.MAIN) { Icon(Icons.Default.Explore, "Explorar") },
        BottomNavItem("Mapa", "map") { Icon(Icons.Default.Map, "Mapa") },
        BottomNavItem("Favoritos", "favorites") { Icon(Icons.Default.Favorite, "Favoritos") },
        BottomNavItem("Perfil", "profile") { Icon(Icons.Default.Person, "Perfil") }
    )

    val bottomBarRoutes = setOf(Screen.MAIN, "map", "favorites", "profile")
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = item.icon,
                            label = { Text(item.label) },
                            selected = navBackStackEntry?.destination?.hierarchy
                                ?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.SPLASH) {
                SplashScreen(
                    onReady = { destination ->
                        navController.navigate(destination) {
                            popUpTo(Screen.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.LOGIN_WITH_RETURN,
                arguments = listOf(navArgument("returnRoute") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val returnRoute = backStackEntry.arguments?.getString("returnRoute")
                LoginScreen(
                    onLoginSuccess = {
                        val destination = returnRoute ?: Screen.MAIN
                        navController.navigate(destination) {
                            popUpTo(Screen.LOGIN_WITH_RETURN) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.REGISTER) }
                )
            }

            composable(Screen.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.MAIN) {
                            popUpTo(Screen.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.REGISTER) }
                )
            }

            composable(Screen.REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.MAIN) {
                            popUpTo(Screen.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable(Screen.MAIN) {
                ExploreScreen(
                    onEventClick = { navController.navigate(Screen.eventDetail(it)) },
                    onVenueClick = { navController.navigate(Screen.venueDetail(it)) }
                )
            }

            composable("map") {
                MapScreen(
                    onVenueClick = { navController.navigate(Screen.venueDetail(it)) }
                )
            }

            composable("favorites") {
                FavoritesScreen(
                    onEventClick = { navController.navigate(Screen.eventDetail(it)) }
                )
            }

            composable("profile") {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onEventClick = { navController.navigate(Screen.eventDetail(it)) }
                )
            }

            composable(
                route = Screen.EVENT_DETAIL,
                arguments = listOf(navArgument("eventId") { type = NavType.IntType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments!!.getInt("eventId")
                EventDetailScreen(
                    onBack = { navController.popBackStack() },
                    onVenueClick = { navController.navigate(Screen.venueDetail(it)) },
                    onLoginRequired = {
                        navController.navigate(Screen.loginWithReturn(Screen.eventDetail(eventId)))
                    }
                )
            }

            composable(
                route = Screen.VENUE_DETAIL,
                arguments = listOf(navArgument("venueId") { type = NavType.IntType })
            ) {
                VenueDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEventClick = { navController.navigate(Screen.eventDetail(it)) }
                )
            }
        }
    }
}
