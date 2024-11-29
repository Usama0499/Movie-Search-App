package com.usama.moviesearchapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.usama.moviesearchapp.data.local.db.AppDatabase
import com.usama.moviesearchapp.data.models.remote.Movie
import retrofit2.HttpException
import java.io.IOException

private const val MOVIE_STARTING_PAGE_INDEX = 1

class MoviePagingSource(
    private val movieApi: MovieApi,
    private val query: String,
    private val appDatabase: AppDatabase
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val position = params.key ?: MOVIE_STARTING_PAGE_INDEX

        return try {
            val response = movieApi.searchMovies(query, position)
            /*val movies = response.Search?: emptyList()*/
            val movies = response.Search?.map { movie ->
                val isBookmarked = appDatabase.bookmarkDao().getBookmarkById(movie.imdbID) != null
                movie.copy(isBookmarked = isBookmarked)
            } ?: emptyList()

            LoadResult.Page(
                data = movies,
                prevKey = if (position == MOVIE_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (movies.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition
    }
}