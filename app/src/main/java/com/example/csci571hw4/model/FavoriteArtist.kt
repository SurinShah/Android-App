package com.example.csci571hw4.model

data class FavoriteArtist(
    val artistId: String,
    val title: String,
    val thumbnail: String?,
    val nationality: String?,
    val birth: String?,
    val timeAgo: String?,
    val addedAt: String?
)
