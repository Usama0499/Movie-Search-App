package com.usama.moviesearchapp.ui.common.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.usama.moviesearchapp.data.MovieRepository
import com.usama.moviesearchapp.data.models.remote.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedMovieViewModel @Inject constructor(
    private val repository: MovieRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

    // Expose search results
    private val _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>> = _movies

    // LiveData for all bookmarked movies
    val bookmarkedMovies: LiveData<PagingData<Movie>> = repository.getAllBookmarks()
        .map { pagingData ->
            pagingData.map { bookmarkedMovieEntity ->
                bookmarkedMovieEntity.toMovie()  // Map each entity to a Movie object
            }
        }
        .cachedIn(viewModelScope)

    init {
        currentQuery.switchMap { queryString ->
            repository.getSearchResults(queryString.trim()).cachedIn(viewModelScope)
        }.observeForever {
            _movies.value = it
        }
    }

    fun searchMovies(query: String) {
        currentQuery.value = query
    }

    fun refreshMovies() {
        currentQuery.value = currentQuery.value
    }

    fun toggleBookmark(movie: Movie) {
        val updatedMovie = movie.copy(isBookmarked = !movie.isBookmarked)
        viewModelScope.launch {
            Log.d("MovieViewModel", "bookmarkMovie: $updatedMovie")
            if (updatedMovie.isBookmarked) {
                repository.bookmarkMovie(updatedMovie)
            }else {
                repository.unbookmarkMovie(updatedMovie)
            }

            // Trigger a partial update
            _movies.value = _movies.value?.map { item ->
                if (item.imdbID == updatedMovie.imdbID) updatedMovie else item
            }
        }
    }

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = "batman"
    }
}