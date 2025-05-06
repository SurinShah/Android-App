package com.example.csci571hw4.network.service

import com.example.csci571hw4.model.FavoriteArtist
import retrofit2.Response
import retrofit2.http.*

data class AddFavoriteRequest(
    val artistId: String,
    val title: String,
    val thumbnail: String?,
    val birth: String?,
    val nationality: String?
)

interface UserApiService {

    @GET("user/favorites/check/{artistId}")
    suspend fun isFavorite(
        @Path("artistId") artistId: String
    ): Response<Boolean>

    @GET("user/favorites")
    suspend fun getFavorites(): Response<List<FavoriteArtist>>

    @POST("user/favorites")
    suspend fun addFavorite(
        @Body request: AddFavoriteRequest
    ): Response<Unit>

    @DELETE("user/favorites/{artistId}")
    suspend fun removeFavorite(
        @Path("artistId") artistId: String
    ): Response<Unit>

}
