package com.example.csci571hw4.network.model

import com.example.csci571hw4.network.model.ThumbnailLink

data class Artwork(
    val id: String,
    val title: String,
    val date: String?,
    val _links: ArtworkLinks?,
    val categories: List<Category>? = null
) {
    val imageUrl: String?
        get() = _links?.thumbnail?.href
}

data class ArtworkLinks(
    val thumbnail: ThumbnailLink?
)
