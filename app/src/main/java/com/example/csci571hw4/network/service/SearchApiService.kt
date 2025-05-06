package com.example.csci571hw4.network.service

import com.example.csci571hw4.network.model.Artist
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    @GET("search")
    suspend fun searchArtists(@Query("q") query: String): List<Artist>
}
