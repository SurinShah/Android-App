package com.example.csci571hw4.ui.tabs

import com.example.csci571hw4.network.model.Artwork
import com.example.csci571hw4.network.model.Category
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArtworksTab(
    artworks: List<Artwork>,
    categories: List<Category>,
    selectedArtworkTitle: String?,
    isLoading: Boolean,
    isModalOpen: Boolean,
    onCloseModal: () -> Unit,
    onViewCategories: (artworkId: String, title: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        artworks.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 40.dp)
                    .background(Color(0xFFDDE7F3), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Artworks",
                    modifier = Modifier.padding(12.dp),
                    color = Color.Black
                )
            }
        }

        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(artworks) { artwork ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            AsyncImage(
                                model = artwork.imageUrl,
                                contentDescription = artwork.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(500.dp),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = artwork.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = artwork.date.orEmpty(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { onViewCategories(artwork.id, artwork.title) },
                                    shape = RoundedCornerShape(30.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475D87))
                                ) {
                                    Text("View Categories", color = Color.White)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (isModalOpen) {
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { categories.size })

        AlertDialog(
            onDismissRequest = onCloseModal,
            confirmButton = {
                TextButton(
                    onClick = onCloseModal,
                    modifier = Modifier
                        .background(Color(0xFF475D87), RoundedCornerShape(30.dp))
                        .height(36.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Close", color = Color.White)
                }
            },
            title = {
                Text("Categories")
            },
            text = {
                if (categories.isEmpty()) {
                    Text("No categories available")
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            },
                            modifier = Modifier.width(24.dp)
                        ) {
                            Text("<", style = MaterialTheme.typography.titleLarge)
                        }

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) { page ->
                            val category = categories[page]
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AsyncImage(
                                        model = category.imageUrl ?: category._links?.thumbnail?.href,
                                        contentDescription = category.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = category.name ?: "Unnamed",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .verticalScroll(rememberScrollState())
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = category.description ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }


                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (pagerState.currentPage < categories.size - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            },
                            modifier = Modifier.width(24.dp)
                        ) {
                            Text(">", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}