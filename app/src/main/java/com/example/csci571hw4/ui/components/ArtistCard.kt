package com.example.csci571hw4.ui.components

import com.example.csci571hw4.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ArtistCard(
    title: String,
    thumbnailUrl: String?,
    birth: String? = null,
    nationality: String? = null,
    onClick: () -> Unit,
    showStar: Boolean = false,
    isFavorited: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null,
    cardColor: Color = MaterialTheme.colorScheme.primary
) {
    val fallbackPainter = painterResource(R.drawable.artsy_logo_foreground)
    val isFallback = thumbnailUrl.isNullOrBlank()
    val textColor = if (cardColor.luminance() > 0.5f) Color(0xFF0D1A26) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (isFallback) {
                    Image(
                        painter = fallbackPainter,
                        contentDescription = "Fallback Artist Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Artist Image",
                        contentScale = ContentScale.Crop,
                        error = fallbackPainter,
                        fallback = fallbackPainter,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (showStar) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(cardColor)
                            .clickable { onToggleFavorite?.invoke() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isFavorited) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Favorite",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_star_outline),
                                contentDescription = "Not Favorite",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardColor)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )

                    if (!birth.isNullOrBlank() || !nationality.isNullOrBlank()) {
                        Text(
                            text = listOfNotNull(birth, nationality).joinToString(" • "),
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.9f)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Arrow",
                    tint = textColor
                )
            }
        }
    }
}

@Preview
@Composable
fun TestStarIcons() {
    Row(Modifier.padding(16.dp)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_star_outline),
            contentDescription = "Outlined Star",
            tint = Color.Black,
            modifier = Modifier
                .size(32.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Filled Star",
            tint = Color(0xFFFFC107),
            modifier = Modifier
                .size(32.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                .padding(4.dp)
        )
    }
}
