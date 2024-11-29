package com.usama.moviesearchapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.usama.moviesearchapp.data.local.db.AppDatabase
import com.usama.moviesearchapp.data.models.db.BookmarkedMovieEntity
import com.usama.moviesearchapp.data.models.remote.Movie
import com.usama.moviesearchapp.data.remote.MovieApi
import com.usama.moviesearchapp.data.remote.MoviePagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieApi: MovieApi,
    private val appDatabase: AppDatabase
) {

    fun getSearchResults(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviePagingSource(movieApi, query, appDatabase) }
        ).liveData


    suspend fun bookmarkMovie(movie: Movie) {
        val bookmarkedMovie = BookmarkedMovieEntity(
            imdbID = movie.imdbID,
            Poster = movie.Poster,
            Title = movie.Title,
            Type = movie.Type,
            Year = movie.Year
        )
        appDatabase.bookmarkDao().insert(bookmarkedMovie)
    }

    suspend fun unbookmarkMovie(movie: Movie) {
        val bookmarkedMovie = BookmarkedMovieEntity(
            imdbID = movie.imdbID,
            Poster = movie.Poster,
            Title = movie.Title,
            Type = movie.Type,
            Year = movie.Year
        )
        appDatabase.bookmarkDao().delete(bookmarkedMovie)
    }

    suspend fun isBookmarked(id: String): Boolean {
        return appDatabase.bookmarkDao().getBookmarkById(id) != null
    }


    // Get all bookmarks as a PagingSource
    fun getAllBookmarks() = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { appDatabase.bookmarkDao().getAllBookmarks() }
    ).liveData

}