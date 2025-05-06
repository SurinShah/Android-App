package com.example.csci571hw4.network.model

data class Category(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val _links: Links?
)

data class Thumbnail(
    val href: String
)
