package com.example.csci571hw4.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci571hw4.network.ApiClient
import com.example.csci571hw4.network.model.Artist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _results = MutableStateFlow<List<Artist>>(emptyList())
    val results: StateFlow<List<Artist>> = _results

    fun searchArtists(query: String) {
        viewModelScope.launch {
            try {
                val artists = ApiClient.searchService.searchArtists(query)
                Log.d("SearchVM", "Artists received: ${artists.size}")
                artists.forEach { artist ->
                    Log.d("SearchVM", "Artist: ${artist.title}, thumbnail: ${artist.thumbnail}")
                }
                _results.value = artists
            } catch (e: Exception) {
                Log.e("SearchVM", "Search failed: ${e.message}", e)
                _results.value = emptyList()
            }
        }
    }

    fun clearResults() {
        _results.value = emptyList()
    }
}