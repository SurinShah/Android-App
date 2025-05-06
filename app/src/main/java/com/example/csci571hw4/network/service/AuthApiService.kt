package com.example.csci571hw4.network.service

import com.example.csci571hw4.network.model.LoginRequest
import com.example.csci571hw4.network.model.LoginResponse
import com.example.csci571hw4.network.model.RegisterRequest

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.Response
import okhttp3.ResponseBody

interface AuthApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ResponseBody>

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Void>

    @GET("/api/auth/me")
    suspend fun getMeInfo(): MeResponse

    data class MeResponse(
        val id: String,
        val fullName: String,
        val email: String,
        val profileImageUrl: String
    )

    @GET("/api/auth/me")
    suspend fun getMe(): Response<Void>

    @POST("/api/auth/logout")
    suspend fun logout(): Response<Void>

    @DELETE("/api/auth/delete")
    suspend fun deleteAccount(): Response<Void>

}
