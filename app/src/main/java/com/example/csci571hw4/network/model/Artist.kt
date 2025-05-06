package com.example.csci571hw4.network.model

import com.example.csci571hw4.network.model.ThumbnailLink

data class Artist(
    val id: String?,
    val name: String?,
    val title: String?,
    val nationality: String?,
    val birthday: String?,
    val deathday: String?,
    val biography: String?,
    val _links: Links?,
    val timeAgo: String?,
) {
    val thumbnail: String?
        get() = _links?.thumbnail?.href
}

data class Links(
    val thumbnail: ThumbnailLink?
)

