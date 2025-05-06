package com.example.csci571hw4.network

import android.content.Context
import com.example.csci571hw4.network.cookie.PersistentCookieJar
import com.example.csci571hw4.network.service.AuthApiService
import com.example.csci571hw4.network.service.SearchApiService
import com.example.csci571hw4.network.service.ArtistApiService
import com.example.csci571hw4.network.service.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private lateinit var retrofit: Retrofit
    private lateinit var cookieJar: PersistentCookieJar

    fun initialize(context: Context) {
        cookieJar = PersistentCookieJar(context)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(logging)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://csci571-a3-surin.uw.r.appspot.com/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val searchService: SearchApiService by lazy { retrofit.create(SearchApiService::class.java) }
    val artistService: ArtistApiService by lazy { retrofit.create(ArtistApiService::class.java) }
    val userService: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
}
