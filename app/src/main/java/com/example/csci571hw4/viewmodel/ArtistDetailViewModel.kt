package com.example.csci571hw4.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci571hw4.network.ApiClient
import com.example.csci571hw4.network.model.Artist
import com.example.csci571hw4.network.model.Artwork
import com.example.csci571hw4.network.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.csci571hw4.network.service.AddFavoriteRequest

class ArtistDetailViewModel : ViewModel() {

    private val artistService = ApiClient.artistService
    private val userService = ApiClient.userService
    private val artworkService = ApiClient.searchService

    private val _artist = MutableStateFlow<Artist?>(null)
    val artist: StateFlow<Artist?> = _artist

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _artworks = MutableStateFlow<List<Artwork>>(emptyList())
    val artworks: StateFlow<List<Artwork>> = _artworks

    private val _isArtworksLoading = MutableStateFlow(false)
    val isArtworksLoading: StateFlow<Boolean> = _isArtworksLoading

    private val _similarArtists = MutableStateFlow<List<Artist>>(emptyList())
    val similarArtists: StateFlow<List<Artist>> = _similarArtists

    private val _isSimilarLoading = MutableStateFlow(false)
    val isSimilarLoading: StateFlow<Boolean> = _isSimilarLoading

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isCategoriesLoading = MutableStateFlow(false)
    val isCategoriesLoading: StateFlow<Boolean> = _isCategoriesLoading

    private val _isCategoriesModalOpen = MutableStateFlow(false)
    val isCategoriesModalOpen: StateFlow<Boolean> = _isCategoriesModalOpen

    private val _selectedArtworkTitle = MutableStateFlow("")
    val selectedArtworkTitle: StateFlow<String> = _selectedArtworkTitle

    private var currentArtistId: String? = null

    fun loadArtist(artistId: String) {
        currentArtistId = artistId
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("ArtistDetailVM", "Fetching artist with ID: $artistId")
                val result = artistService.getArtist(artistId)

                Log.d("ArtistDetailVM", "Fetched artist: ${result.name}, birth=${result.birthday}, nationality=${result.nationality}")
                _artist.value = result

                val response = userService.isFavorite(artistId)
                _isFavorite.value = response.body() == true
            } catch (e: Exception) {
                Log.e("ArtistDetailVM", "Error loading artist: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(
        artistId: String = currentArtistId ?: "",
        title: String = "",
        thumbnail: String = "",
        birth: String? = null,
        nationality: String? = null
    ) {
        viewModelScope.launch {
            try {
                Log.d("ArtistDetailVM", "Toggling favorite for $title [ID=$artistId]")
                Log.d("ArtistDetailVM", "Birth=$birth, Nationality=$nationality")

                if (_isFavorite.value) {
                    userService.removeFavorite(artistId)
                    _isFavorite.value = false
                    Log.d("ArtistDetailVM", "Removed from favorites")
                } else {
                    val request = AddFavoriteRequest(
                        artistId = artistId,
                        title = title,
                        thumbnail = thumbnail,
                        birth = birth,
                        nationality = nationality
                    )
                    userService.addFavorite(request)
                    _isFavorite.value = true
                    Log.d("ArtistDetailVM", "Added to favorites with request: $request")
                }
            } catch (e: Exception) {
                Log.e("ArtistDetailVM", "Favorite toggle failed: ${e.message}", e)
            }
        }
    }

    fun loadArtworks(artistId: String) {
        _isArtworksLoading.value = true
        viewModelScope.launch {
            try {
                val result = artistService.getArtworks(artistId)
                _artworks.value = result
                Log.d("ArtistDetailVM", "Loaded ${result.size} artworks for artist $artistId")
            } catch (e: Exception) {
                Log.e("ArtistDetailVM", "Error loading artworks: ${e.message}")
            } finally {
                _isArtworksLoading.value = false
            }
        }
    }

    fun loadSimilarArtists(artistId: String) {
        _isSimilarLoading.value = true
        viewModelScope.launch {
            try {
                val result = artistService.getSimilarArtists(artistId)
                _similarArtists.value = result
                Log.d("ArtistDetailVM", "Loaded ${result.size} similar artists for artist $artistId")
            } catch (e: Exception) {
                Log.e("ArtistDetailVM", "Error loading similar artists: ${e.message}")
            } finally {
                _isSimilarLoading.value = false
            }
        }
    }

    fun loadCategories(artworkId: String, artworkTitle: String) {
        viewModelScope.launch {
            _selectedArtworkTitle.value = artworkTitle
            _isCategoriesModalOpen.value = true
            try {
                Log.d("ArtistDetailVM", "Fetching categories for artwork: $artworkId")
                val categories = artistService.getCategoriesForArtwork(artworkId)
                Log.d("ArtistDetailVM", "Categories fetched: ${categories.size}")
                _categories.value = categories
            } catch (e: Exception) {
                _categories.value = emptyList()
                Log.e("ArtistDetailVM", "Error fetching categories: ${e.message}", e)
            }
        }
    }

    fun closeCategoriesModal() {
        _isCategoriesModalOpen.value = false
    }
}
