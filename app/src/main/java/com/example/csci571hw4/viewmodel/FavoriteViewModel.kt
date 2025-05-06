package com.example.csci571hw4.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci571hw4.model.FavoriteArtist
import com.example.csci571hw4.network.service.AddFavoriteRequest
import com.example.csci571hw4.network.ApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class FavoriteViewModel : ViewModel() {
    private val _favorites = MutableStateFlow<List<FavoriteArtist>>(emptyList())
    val favorites: StateFlow<List<FavoriteArtist>> = _favorites

    private val userService = ApiClient.userService

    init {
        loadFavorites()
        startLiveTimer()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val response: Response<List<FavoriteArtist>> = userService.getFavorites()
                if (response.isSuccessful) {
                    val rawFavorites = response.body() ?: emptyList()

                    rawFavorites.forEach { artist ->
                        println("DEBUG: Fetched artist ${artist.title}, birth=${artist.birth}, nationality=${artist.nationality}")
                    }

                    _favorites.value = rawFavorites.map { artist ->
                        val timeAgo = getRelativeTime(artist.addedAt)
                        artist.copy(timeAgo = timeAgo)
                    }.reversed()
                } else {
                    println("DEBUG: Failed to fetch favorites - response code: ${response.code()}")
                    _favorites.value = emptyList()
                }
            } catch (e: Exception) {
                println("DEBUG: Exception while fetching favorites - ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun getRelativeTime(isoString: String?): String? {
        if (isoString == null) return null
        return try {
            val formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault())
            val addedTime = Instant.from(formatter.parse(isoString))
            val now = Instant.now()
            val duration = Duration.between(addedTime, now)
            val seconds = duration.seconds
            when {
                seconds < 60 -> "$seconds seconds ago"
                seconds < 3600 -> "${seconds / 60} minutes ago"
                seconds < 86400 -> "${seconds / 3600} hours ago"
                else -> "${seconds / 86400} days ago"
            }
        } catch (e: Exception) {
            println("DEBUG: Failed to parse addedAt time - ${e.message}")
            null
        }
    }

    fun toggleFavorite(
        artistId: String,
        title: String,
        thumbnail: String?,
        birth: String?,
        timeAgo: String?,
        nationality: String?,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                val exists = _favorites.value.any { it.artistId == artistId }
                if (exists) {
                    userService.removeFavorite(artistId)
                    onComplete?.invoke(false)
                } else {
                    println("DEBUG: Adding favorite: id=$artistId, title=$title, birth=$birth, nationality=$nationality")

                    userService.addFavorite(
                        AddFavoriteRequest(
                            artistId = artistId,
                            title = title,
                            thumbnail = thumbnail,
                            birth = birth,
                            nationality = nationality
                        )
                    )
                    onComplete?.invoke(true)
                }
                loadFavorites()
            } catch (e: Exception) {
                println("DEBUG: toggleFavorite failed - ${e.message}")
                e.printStackTrace()
                onComplete?.invoke(false)
            }
        }
    }

    fun removeFavorite(artistId: String) {
        viewModelScope.launch {
            try {
                println("DEBUG: Removing favorite with id=$artistId")
                userService.removeFavorite(artistId)
                loadFavorites()
            } catch (e: Exception) {
                println("DEBUG: removeFavorite failed - ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun startLiveTimer() {
        viewModelScope.launch {
            while (true) {
                _favorites.value = _favorites.value.map { artist ->
                    val updatedTimeAgo = getRelativeTime(artist.addedAt)
                    artist.copy(timeAgo = updatedTimeAgo)
                }
                delay(1000)
            }
        }
    }
}
