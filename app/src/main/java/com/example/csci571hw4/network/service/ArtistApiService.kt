package com.example.csci571hw4.network.service

import com.example.csci571hw4.network.model.Artist
import com.example.csci571hw4.network.model.Artwork
import com.example.csci571hw4.network.model.Category
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtistApiService {

    @GET("artist/{id}")
    suspend fun getArtist(@Path("id") id: String): Artist

    @GET("artist/{id}/artworks")
    suspend fun getArtworks(@Path("id") id: String): List<Artwork>

    @GET("artist/{id}/similar")
    suspend fun getSimilarArtists(@Path("id") id: String): List<Artist>

    @GET("artwork/{id}/categories")
    suspend fun getCategoriesForArtwork(@Path("id") artworkId: String): List<Category>

}
