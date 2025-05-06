package com.example.csci571hw4.network.model

data class AddFavoriteRequest(
    val artistId: String,
    val title: String,
    val thumbnail: String?,
    val birth: String?,
    val nationality: String?
)