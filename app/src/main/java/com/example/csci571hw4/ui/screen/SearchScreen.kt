package com.example.csci571hw4.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.csci571hw4.ui.components.ArtistCard
import com.example.csci571hw4.viewmodel.AuthViewModel
import com.example.csci571hw4.viewmodel.FavoriteViewModel
import com.example.csci571hw4.viewmodel.SearchViewModel
import com.example.csci571hw4.network.ApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    onBack: () -> Unit,
    viewModel: SearchViewModel,
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    favoriteViewModel: FavoriteViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onArtistClick: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    val textColor = MaterialTheme.colorScheme.onPrimary
    val results by viewModel.results.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val favorites by favoriteViewModel.favorites.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    LaunchedEffect(isAuthenticated, results) {
        if (isAuthenticated) {
            favoriteViewModel.loadFavorites()
        }
    }

    val cardColor = MaterialTheme.colorScheme.primary

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .background(cardColor)
                    .padding(WindowInsets.statusBars.asPaddingValues())
            ) {
                TextField(
                    value = query,
                    onValueChange = {
                        query = it
                        debounceJob?.cancel()
                        if (query.length >= 3) {
                            debounceJob = scope.launch {
                                delay(300)
                                viewModel.searchArtists(query)
                            }
                        } else {
                            viewModel.clearResults()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    placeholder = {
                        Text("Search artists…", color = textColor.copy(alpha = 0.6f), fontSize = 16.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = textColor)
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            query = ""
                            viewModel.clearResults()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = textColor)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        cursorColor = textColor
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(results) { artist ->
                    val isFavorited = favorites.any { it.artistId == artist.id }

                    ArtistCard(
                        title = artist.title ?: "Unknown",
                        thumbnailUrl = artist.thumbnail,
                        onClick = {
                            val id = artist.id ?: return@ArtistCard
                            navController.navigate("artistDetail/$id")
                        },
                        showStar = isAuthenticated,
                        isFavorited = isFavorited,
                        cardColor = cardColor,
                        onToggleFavorite = {
                            val artistId = artist.id ?: return@ArtistCard
                            scope.launch {
                                try {
                                    val fullArtist = ApiClient.artistService.getArtist(artistId)
                                    Log.d("SearchScreen", "Fetched full artist: ${fullArtist.name}, birth=${fullArtist.birthday}, nationality=${fullArtist.nationality}")

                                    favoriteViewModel.toggleFavorite(
                                        artistId = fullArtist.id.orEmpty(),
                                        title = fullArtist.name.orEmpty(),
                                        thumbnail = fullArtist.thumbnail.orEmpty(),
                                        birth = fullArtist.birthday,
                                        nationality = fullArtist.nationality,
                                        timeAgo = null,
                                        onComplete = { added ->
                                            snackbarMessage = if (added) {
                                                "Added to favorites"
                                            } else {
                                                "Removed from favorites"
                                            }

                                            navController.previousBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("shouldRefreshFavorites", true)
                                        }
                                    )
                                } catch (e: Exception) {
                                    Log.e("SearchScreen", "Failed to fetch full artist info: ${e.message}", e)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
