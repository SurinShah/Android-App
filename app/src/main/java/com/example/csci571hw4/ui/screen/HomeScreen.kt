package com.example.csci571hw4.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import android.R.attr.duration
import coil.compose.AsyncImage
import com.example.csci571hw4.model.FavoriteArtist
import com.example.csci571hw4.utils.getCurrentDate
import com.example.csci571hw4.viewmodel.AuthViewModel
import com.example.csci571hw4.viewmodel.FavoriteViewModel
import kotlinx.coroutines.launch

@Composable
fun FavoriteArtistItem(
    artist: FavoriteArtist,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artist.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                val subtitle = buildString {
                    if (!artist.nationality.isNullOrBlank()) append(artist.nationality)
                    if (artist.birth != null) {
                        if (!artist.nationality.isNullOrBlank()) append(", ")
                        append(artist.birth)
                    }
                }

                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                artist.timeAgo?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Details",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    onSearchClick: () -> Unit,
    onArtistClick: (String) -> Unit,
    authViewModel: AuthViewModel,
    navController: NavController,
    favoriteViewModel: FavoriteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val profileImageUrl by authViewModel.profileImageUrl.collectAsState()
    val favorites by favoriteViewModel.favorites.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = if (primaryColor.luminance() > 0.5f) Color.Black else Color.White

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val shouldRefresh = backStackEntry.savedStateHandle.get<Boolean>("shouldRefreshFavorites") ?: false
            if (shouldRefresh && isLoggedIn) {
                favoriteViewModel.loadFavorites()
                backStackEntry.savedStateHandle["shouldRefreshFavorites"] = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Artist Search", color = textColor) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor),
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = textColor)
                    }

                    if (isLoggedIn && profileImageUrl != null) {
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                AsyncImage(
                                    model = profileImageUrl,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(28.dp).clip(CircleShape)
                                )
                            }

                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Log out", color = Color(0xFF1A237E)) },
                                    onClick = {
                                        authViewModel.logout()
                                        menuExpanded = false
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Logged out successfully",
                                                duration = SnackbarDuration.Long
                                            )
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete Account", color = Color(0xFFB00020)) },
                                    onClick = {
                                        menuExpanded = false
                                        authViewModel.deleteAccount(
                                            onSuccess = {
                                                navController.navigate("home") {
                                                    popUpTo("home") { inclusive = true }
                                                }
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        message = "Account Deleted Successfully",
                                                        duration = SnackbarDuration.Long
                                                    )                                                }
                                            },
                                            onError = { error -> println("Delete failed: $error") }
                                        )
                                    }
                                )
                            }
                        }
                    } else {
                        IconButton(onClick = onLoginClick) {
                            Icon(Icons.Default.Person, contentDescription = "User", tint = textColor)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = getCurrentDate(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F1F1))
                        .padding(vertical = 8.dp)
                        .offset(x = (-16).dp)
                ) {
                    Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                }
            }

            when {
                !isLoggedIn -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = onLoginClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475D87)),
                            shape = RoundedCornerShape(30.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 7.dp)
                        ) {
                            Text("Log in to see favorites", color = Color.White)
                        }
                    }
                }

                favorites.isEmpty() -> {
                    val isDark = isSystemInDarkTheme()
                    val backgroundColor = if (isDark) Color(0xFF475D87) else Color(0xFFDDE9FA)

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No favorites",
                            color = if (isDark) Color.White else Color.Black,
                            modifier = Modifier.padding(vertical = 16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {
                    LazyColumn {
                        items(favorites) { artist ->
                            FavoriteArtistItem(
                                artist = artist,
                                onClick = { onArtistClick(artist.artistId) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Powered by Artsy",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.artsy.net/"))
                        context.startActivity(intent)
                    }
            )
        }
    }
}
