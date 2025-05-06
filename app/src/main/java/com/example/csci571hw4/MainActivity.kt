package com.example.csci571hw4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.csci571hw4.network.ApiClient
import com.example.csci571hw4.ui.nav.NavGraph
import com.example.csci571hw4.ui.theme.CSCI571HW4Theme
import com.example.csci571hw4.viewmodel.AuthViewModel
import com.example.csci571hw4.viewmodel.FavoriteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiClient.initialize(applicationContext)

        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val favoriteViewModel: FavoriteViewModel = viewModel()

            val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
            val favorites by favoriteViewModel.favorites.collectAsState()

            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                authViewModel.checkAuthStatus()
            }

            LaunchedEffect(isAuthenticated) {
                if (isAuthenticated) {
                    favoriteViewModel.loadFavorites()
                }
            }

            CSCI571HW4Theme {
                NavGraph(
                    navController = navController,
                    isLoggedIn = isAuthenticated,
                    authViewModel = authViewModel,
                    favoriteViewModel = favoriteViewModel
                )
            }
        }
    }
}
