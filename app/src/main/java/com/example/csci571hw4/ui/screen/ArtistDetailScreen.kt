package com.example.csci571hw4.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.csci571hw4.R
import com.example.csci571hw4.ui.tabs.ArtworksTab
import com.example.csci571hw4.ui.tabs.DetailsTab
import com.example.csci571hw4.ui.tabs.SimilarArtistsTab
import com.example.csci571hw4.viewmodel.ArtistDetailViewModel
import com.example.csci571hw4.viewmodel.AuthViewModel
import com.example.csci571hw4.viewmodel.FavoriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    navController: NavController,
    artistId: String,
    viewModel: ArtistDetailViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel()
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val artist by viewModel.artist.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val artworks by viewModel.artworks.collectAsState()
    val isArtworksLoading by viewModel.isArtworksLoading.collectAsState()
    val similarArtists by viewModel.similarArtists.collectAsState()
    val isSimilarLoading by viewModel.isSimilarLoading.collectAsState()
    val favoriteList by favoriteViewModel.favorites.collectAsState()

    val categories by viewModel.categories.collectAsState()
    val isModalOpen by viewModel.isCategoriesModalOpen.collectAsState()
    val selectedArtworkTitle by viewModel.selectedArtworkTitle.collectAsState()

    val isFavorited = remember(artist, favoriteList) {
        artist?.id?.let { id -> favoriteList.any { it.artistId == id } } ?: false
    }

    val favoriteIds = remember(favoriteList) {
        favoriteList.map { it.artistId }.toSet()
    }

    var selectedTab by remember { mutableStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = if (primaryColor.luminance() > 0.5f) Color.Black else Color.White

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    LaunchedEffect(artistId) {
        viewModel.loadArtist(artistId)
        viewModel.loadArtworks(artistId)
        viewModel.loadSimilarArtists(artistId)
        favoriteViewModel.loadFavorites()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = textColor,
                    navigationIconContentColor = textColor,
                    actionIconContentColor = textColor
                ),
                title = { Text(text = artist?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isAuthenticated && artist != null) {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(primaryColor)
                                .clickable {
                                    favoriteViewModel.toggleFavorite(
                                        artistId = artist?.id.orEmpty(),
                                        title = artist?.name.orEmpty(),
                                        thumbnail = artist?.thumbnail.orEmpty(),
                                        birth = artist?.birthday,
                                        timeAgo = null,
                                        nationality = artist?.nationality,
                                        onComplete = { added ->
                                            snackbarMessage =
                                                if (added) "Added to favorites" else "Removed from favorites"
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isFavorited) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Favorited",
                                    tint = textColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_star_outline),
                                    contentDescription = "Not Favorite",
                                    tint = textColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val tabs = if (isAuthenticated) listOf("Details", "Artworks", "Similar") else listOf("Details", "Artworks")

                if (artist == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Artist not found or failed to load.")
                    }
                    return@Column
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    contentColor = primaryColor
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
                        val activeColor = if (isLightTheme) Color(0xFF205375) else Color(0xFF9FC5F8)
                        val inactiveColor = if (isLightTheme) Color(0xFF5F738E) else Color(0xFF9FC5F8).copy(alpha = 0.6f)

                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            selectedContentColor = activeColor,
                            unselectedContentColor = inactiveColor,
                            text = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(
                                            id = when (title) {
                                                "Details" -> R.drawable.info
                                                "Artworks" -> R.drawable.photo
                                                "Similar" -> R.drawable.person_search
                                                else -> R.drawable.info
                                            }
                                        ),
                                        contentDescription = "$title Icon",
                                        modifier = Modifier.size(20.dp),
                                        tint = if (selectedTab == index) activeColor else inactiveColor
                                    )
                                    Text(text = title)
                                }
                            }
                        )
                    }
                }

                when (tabs[selectedTab]) {
                    "Details" -> artist?.let { DetailsTab(it) }
                    "Artworks" -> ArtworksTab(
                        artworks = artworks,
                        categories = categories,
                        selectedArtworkTitle = selectedArtworkTitle,
                        isLoading = isArtworksLoading,
                        isModalOpen = isModalOpen,
                        onCloseModal = { viewModel.closeCategoriesModal() },
                        onViewCategories = { id, title -> viewModel.loadCategories(id, title) }
                    )
                    "Similar" -> SimilarArtistsTab(
                        artists = similarArtists,
                        favorites = favoriteIds,
                        onToggleFavorite = { similarArtist ->
                            favoriteViewModel.toggleFavorite(
                                artistId = similarArtist.id.orEmpty(),
                                title = similarArtist.name.orEmpty(),
                                thumbnail = similarArtist.thumbnail.orEmpty(),
                                birth = similarArtist.birthday,
                                timeAgo = null,
                                nationality = similarArtist.nationality,
                                onComplete = { added ->
                                    snackbarMessage =
                                        if (added) "Added to favorites" else "Removed from favorites"
                                }
                            )
                        },
                        onArtistClick = { artist ->
                            artist.id?.let {
                                navController.navigate("artistDetail/$it")
                            }
                        },
                        isLoading = isSimilarLoading
                    )
                }
            }
        }
    }
}
