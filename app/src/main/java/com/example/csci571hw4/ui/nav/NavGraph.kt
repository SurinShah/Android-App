package com.example.csci571hw4.ui.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.csci571hw4.ui.screen.*
import com.example.csci571hw4.viewmodel.AuthViewModel
import com.example.csci571hw4.viewmodel.FavoriteViewModel
import com.example.csci571hw4.viewmodel.SearchViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean,
    authViewModel: AuthViewModel,
    favoriteViewModel: FavoriteViewModel
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                isLoggedIn = isLoggedIn,
                onLoginClick = { navController.navigate("login") },
                onSearchClick = { navController.navigate("search") },
                onArtistClick = { artistId -> navController.navigate("artistDetail/$artistId") },
                authViewModel = authViewModel,
                navController = navController,
                favoriteViewModel = favoriteViewModel
            )
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("search") {
            val searchViewModel: SearchViewModel = viewModel()
            SearchScreen(
                navController = navController,
                onBack = { navController.popBackStack() },
                viewModel = searchViewModel,
                onArtistClick = { artistId -> navController.navigate("artistDetail/$artistId") },
                authViewModel = authViewModel,
                favoriteViewModel = favoriteViewModel
            )
        }

        composable(
            route = "artistDetail/{artistId}",
            arguments = listOf(navArgument("artistId") { defaultValue = "" })
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
            ArtistDetailScreen(
                navController = navController,
                artistId = artistId,
                authViewModel = authViewModel,
                favoriteViewModel = favoriteViewModel
            )
        }
    }
}